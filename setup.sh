#!/bin/bash

# reference https://qiita.com/rockhopper/items/9070fd8b15a738888ec6

pyenv install anaconda3-5.0.0
pyenv virtualenv anaconda3-5.0.0 spark-env
pyenv local spark-env

pip install --pre toree
pip install jupyter
jupyter toree install
