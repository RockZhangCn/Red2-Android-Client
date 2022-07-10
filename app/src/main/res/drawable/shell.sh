#!/bin/bash

for name in `ls *.png`
do
  mv $name poker_$name
done
