#!/usr/bin/env python
# coding: utf-8

import os
import urllib
from sklearn.datasets import load_svmlight_file

def get_data():
    url = "https://raw.githubusercontent.com/apache/spark/master/data/mllib/sample_libsvm_data.txt"
    filename = "data.txt"
    if not os.path.isfile(filename):
        urllib.request.urlretrieve(url, "{}".format(filename))
    data = load_svmlight_file(filename)
    return data[0], data[1]

X, y = get_data()

print(X)
