#!/usr/bin/env python3

import os
from subprocess import call, getoutput
from datetime import datetime
import pandas as pd
import matplotlib.pyplot as plt
import re
import platform

benchmarks_results_folder = 'benchmark-results'
result_file_name = 'benchmarks.csv'
info_file_name = 'info.txt'
perf_diff_file_name = 'performance-difference.txt'
baseline_set_impl_name = 'scalaSet'


def get_jvm_version():
    return getoutput('java -version')


def get_cpu_info():
    return getoutput('lscpu')


def run_benchmarks(forks, measurement_iterations, warmup_iterations, result_file_path, benchmarks=None):
    if not benchmarks:
        benchmarks = []
    benchmarks_java_regexp = '.*'
    if len(benchmarks) > 0:
        benchmarks_java_regexp = "|".join(['.*' + benchmark + '.*' for benchmark in benchmarks])
    call(['java', '-jar', 'target/microbenchmarks.jar', benchmarks_java_regexp,
          '-f', str(forks),
          '-i', str(measurement_iterations),
          '-wi', str(warmup_iterations),
          '-rf', 'csv',
          '-rff', result_file_path])


def run_buckets_inspection(result_dir):
    call(['java', '-classpath', 'target/microbenchmarks.jar',  'memory.InspectAverageBucketSizePerElement', result_dir])


def get_benchmark_group(benchmark):
    return re.sub(r'\.[^\.]+$', '', benchmark)


def get_set_name(benchmark):
    return re.search(r'([^\.]+)$', benchmark).group(1)


def plot_bucket_inspection(bucket_inspection_file_path):
    data = pd.read_csv(bucket_inspection_file_path)
    data.plot(x='size', y='averageBucketSizePerElement')


def get_bucket_inspection_file_name(benchmark_group):
    if benchmark_group.endswith('StringsBenchmark'):
        return 'hash-set-with-strings-buckets-size.csv'
    elif benchmark_group.endswith('IntegersBenchmark'):
        return 'hash-set-with-integers-buckets-size.csv'
    else:
        raise RuntimeError('unable to resolve bucket inspection file name for benchmark_group ' + benchmark_group)


def make_charts(result_file_path, output_folder):
    data = pd.read_csv(result_file_path)
    benchmark_groups = data['Benchmark'].map(get_benchmark_group).unique()
    for benchmark_group in benchmark_groups:
        chart_data = data[data['Benchmark'].str.startswith(benchmark_group)]
        chart_data['Set'] = chart_data['Benchmark'].apply(get_set_name)
        sets = chart_data['Set'].unique()
        unit = chart_data['Unit'].unique()[0]

        figure = plt.figure()

        plt.subplot(211)
        for set in sets:
            chart_data[chart_data['Set'] == set].plot(x='Param: size', y='Mean')
        main_data_legend = plt.legend(sets, loc='center left', bbox_to_anchor=(1, 0.5))
        plt.ylabel(unit)

        plt.subplot(212)
        plot_bucket_inspection(os.path.join(output_folder, get_bucket_inspection_file_name(benchmark_group)))
        bucket_inspection_legend = plt.legend(['average bucket size per element'],
                                              loc='center left',
                                              bbox_to_anchor=(1, 0.5))

        additional_artists = [main_data_legend, bucket_inspection_legend]
        figure.savefig(os.path.join(output_folder, benchmark_group + '.png'),
                       additional_artists=additional_artists,
                       bbox_inches='tight')


def make_run_results_folder():
    run_folder_name = datetime.now().strftime('%Y-%m-%d-%H%M%S')
    run_results_folder = os.path.join(benchmarks_results_folder, run_folder_name)
    if not os.path.exists(run_results_folder):
        os.makedirs(run_results_folder)
    else:
        raise RuntimeError('run results directory alredy exists: ' + run_results_folder)
    return run_results_folder


def extract_benchmarks_data_frames(result_file_path):
    data = pd.read_csv(result_file_path)
    benchmark_groups = data['Benchmark'].map(get_benchmark_group).unique()
    benchmarks_data_frames = {}
    for benchmark_group in benchmark_groups:
        benchmark_data = data[data['Benchmark'].str.startswith(benchmark_group)]
        benchmark_data['Set'] = benchmark_data['Benchmark'].apply(get_set_name)

        benchmarks_data_frames[benchmark_group] = benchmark_data.pivot(index='Param: size', columns='Set')

    return benchmarks_data_frames


def calc_performance_difference_percentage(result_file_path):
    benchmarks_differences = {}
    for benchmark, data in extract_benchmarks_data_frames(result_file_path).items():
        mean_data = data['Mean']
        print(benchmark)
        print(mean_data)
        sets = mean_data.columns
        sets = sets[sets != baseline_set_impl_name]
        sets_differences = {}
        for set_name in sets:
            set_data = mean_data[set_name]
            baseline_data = mean_data[baseline_set_impl_name]
            difference = (1 - set_data / baseline_data) * 100
            sets_differences[set_name] = (difference.mean(), difference.min(), difference.max())
        benchmarks_differences[benchmark] = sets_differences

    return benchmarks_differences


def make_performance_difference_file(result_file_path, run_results_folder):
    benchmarks_differences = calc_performance_difference_percentage(result_file_path)
    with open(os.path.join(run_results_folder, perf_diff_file_name), mode='wt') as perf_diff_file:
        perf_diff_file.write('Baseline implementation: %s\n\n' % baseline_set_impl_name)
        for benchmark in benchmarks_differences:
            sets_differences = benchmarks_differences[benchmark]
            perf_diff_file.write('%s:\n' % benchmark)
            for set_name in sets_differences:
                mean, min_diff, max_diff = sets_differences[set_name]
                perf_diff_file.write('\t%s:\taverage %.2f%%, min %.2f%%, max %.2f%%\n' %
                                     (set_name, mean, min_diff, max_diff))
            perf_diff_file.write('\n')


def run_all():
    forks = 1
    measurement_iterations = 1000
    warmup_iterations = 20
    run_results_folder = make_run_results_folder()

    with open(os.path.join(run_results_folder, info_file_name), mode='wt') as info_file:
        info_file.write('--------------Run Info--------------\n')
        info_file.write('Forks: %d\n' % forks)
        info_file.write('Measurement iterations: %d\n' % measurement_iterations)
        info_file.write('Warm up iterations: %d\n' % warmup_iterations)
        info_file.write('--------------JVM Info--------------\n')
        info_file.write(get_jvm_version() + '\n')
        info_file.write('--------------Platform Info--------------\n')
        info_file.write('Platform: %s\n' % platform.platform())
        info_file.write(get_cpu_info() + '\n')

    result_file_path = os.path.join(run_results_folder, result_file_name)
    run_benchmarks(
        result_file_path=result_file_path,
        forks=forks,
        measurement_iterations=measurement_iterations,
        warmup_iterations=warmup_iterations)
    run_buckets_inspection(run_results_folder)
    make_charts(result_file_path, run_results_folder)
    make_performance_difference_file(result_file_path, run_results_folder)


if __name__ == '__main__':
    run_all()
