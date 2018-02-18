#!/usr/bin/env python
# coding: utf-8

import os
import urllib
from sklearn.datasets import load_svmlight_file
from matplotlib import pyplot as plt
import numpy as np

GITHUB_HOST = "https://raw.githubusercontent.com/apache/spark/master/data/mllib/"
BINOMIAL_DATA_URL = GITHUB_HOST + "sample_libsvm_data.txt"
MULTICLASS_DATA_URL = GITHUB_HOST + "sample_multiclass_classification_data.txt"
DATA_FILENAME = "data.txt"
    
def get_data(url, filename):
    if not os.path.isfile(filename):
        urllib.request.urlretrieve(url, "{}".format(filename))
    data = load_svmlight_file(filename)
    return data[0], data[1]

X, y = get_data(MULTICLASS_DATA_URL, DATA_FILENAME)
print(X.indices)
print(X.data)
print(X.indptr)

# plt.scatter(X[0], X[1])
# plt.show()
