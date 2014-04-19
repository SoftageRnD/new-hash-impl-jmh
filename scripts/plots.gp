#!/usr/bin/gnuplot -persist

set datafile separator ','
set term pdf

set style line 1 lt 1 lw 2 pt -1 lc rgb "red"
set style line 2 lt 1 lw 2 pt -1 lc rgb "blue"
set style line 3 lt 1 lw 2 pt -1 lc rgb "green"

set xrange [0:1100000]

set output 'plots.pdf'

set title 'Contains (integers)'
plot 'data.csv' every ::1::10 u 8:5:6 w errorbars ls 1 notitle,\
     '' every ::1::10 u 8:5 w lines ls 1 t 'Immutable Trie Bucket',\
     '' every ::11::20 u 8:5:6 w errorbars ls 2 notitle,\
     '' every ::11::20 u 8:5 w lines ls 2 t 'List Bucket',\
     '' every ::21::30 u 8:5:6 w errorbars ls 3 notitle,\
     '' every ::21::30 u 8:5 w lines ls 3 t 'Scala'

set title 'Contains not existed (integers)'
plot 'data.csv' every ::31::40 u 8:5:6 w errorbars ls 1 notitle,\
     '' every ::31::40 u 8:5 w lines ls 1 t 'Immutable Trie Bucket',\
     '' every ::41::50 u 8:5:6 w errorbars ls 2 notitle,\
     '' every ::41::50 u 8:5 w lines ls 2 t 'List Bucket',\
     '' every ::51::60 u 8:5:6 w errorbars ls 3 notitle,\
     '' every ::51::60 u 8:5 w lines ls 3 t 'Scala'

set xrange [0:60000]

set title 'Contains (strings)'
plot 'data.csv' every ::91::100 u 8:5:6 w errorbars ls 1 notitle,\
     '' every ::91::100 u 8:5 w lines ls 1 t 'Immutable Trie Bucket',\
     '' every ::101::110 u 8:5:6 w errorbars ls 2 notitle,\
     '' every ::101::110 u 8:5 w lines ls 2 t 'List Bucket',\
     '' every ::111::120 u 8:5:6 w errorbars ls 3 notitle,\
     '' every ::111::120 u 8:5 w lines ls 3 t 'Scala'

set title 'Contains not existed (strings)'
plot 'data.csv' every ::61::70 u 8:5:6 w errorbars ls 1 notitle,\
     '' every ::61::70 u 8:5 w lines ls 1 t 'Immutable Trie Bucket',\
     '' every ::71::80 u 8:5:6 w errorbars ls 2 notitle,\
     '' every ::71::80 u 8:5 w lines ls 2 t 'List Bucket',\
     '' every ::81::90 u 8:5:6 w errorbars ls 3 notitle,\
     '' every ::81::90 u 8:5 w lines ls 3 t 'Scala'