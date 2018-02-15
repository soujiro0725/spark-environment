#!/usr/bin/env python
# coding: utf-8

import os
import urllib
from sklearn.datasets import load_svmlight_file
import numpy as np
from matplotlib import pyplot as plt

def get_data():
    url = "https://raw.githubusercontent.com/apache/spark/master/data/mllib/sample_libsvm_data.txt"
    filename = "data.txt"
    if not os.path.isfile(filename):
        urllib.request.urlretrieve(url, "{}".format(filename))
    data = load_svmlight_file(filename)
    return data[0], data[1]

X, y = get_data()
print(X.indices)
print(X.data)
print(X.indptr)

# plt.scatter(X[0], X[1])
# plt.show()
