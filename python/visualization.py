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
#print(X.indices)
#print(X.data)
#print(X.indptr)

X_centered = X - X.mean(axis=0)
U, s, Vt = np.linalg.svd(X_centered)
c1 = Vt.T[:, 0]
c2 = Vt.T[:, 1]
print("c1 is:{}".format(c1))
print("c2 is:{}".format(c2))
W2 = Vt.T[:, :2]
print("W2 is: {}".format(W2))

# plt.scatter(X[0], X[1])
# plt.show()
