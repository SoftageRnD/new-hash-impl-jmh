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


def get_jvm_version():
    return getoutput('java -version')


def get_cpu_info():
    return getoutput('lscpu')


def run_benchmarks(forks, measurement_iterations, warmup_iterations, result_file_path, benchmarks=[]):
    benchmarks_java_regexp = '.*'
    if len(benchmarks) > 0:
        benchmarks_java_regexp = "|".join(['.*' + benchmark + '.*' for benchmark in benchmarks])
    call(['java', '-jar', 'target/microbenchmarks.jar', benchmarks_java_regexp,
          '-f', str(forks),
          '-i', str(measurement_iterations),
          '-wi', str(warmup_iterations),
          '-rf', 'csv',
          '-rff', result_file_path])


def make_charts(result_file_path, output_folder):
    def get_benchmark_group(benchmark):
        return re.sub(r'\.[^\.]+$', '', benchmark)

    def get_set_name(benchmark):
        return re.search(r'([^\.]+)$', benchmark).group(1)

    data = pd.read_csv(result_file_path)
    benchmark_groups = data['Benchmark'].map(get_benchmark_group).unique()
    for benchmark_group in benchmark_groups:
        chart_data = data[data['Benchmark'].str.startswith(benchmark_group)]
        chart_data['Set'] = chart_data['Benchmark'].apply(get_set_name)
        sets = chart_data['Set'].unique()
        unit = chart_data['Unit'].unique()[0]

        figure = plt.figure()
        for set in sets:
            chart_data[chart_data['Set'] == set].plot(x='Param: size', y='Mean')
        plt.legend(sets, loc='upper left')
        plt.ylabel(unit)
        figure.savefig(os.path.join(output_folder, benchmark_group + '.png'))


def make_run_results_folder():
    run_folder_name = datetime.now().strftime('%Y-%m-%d-%H%M%S')
    run_results_folder = os.path.join(benchmarks_results_folder, run_folder_name)
    if not os.path.exists(run_results_folder):
        os.makedirs(run_results_folder)
    else:
        raise RuntimeError('run results directory alredy exists: ' + run_results_folder)
    return run_results_folder


def run_all():
    forks = 1
    measurement_iterations = 100
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
    make_charts(result_file_path, run_results_folder)


if __name__ == '__main__':
    run_all()
