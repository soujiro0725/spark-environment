Spark Environment
=====

- Spark with jupyter notebook
- Spark on Docker

```
# export SPARK_HOME=/Users/<path>/spark-2.1.0-hadoop2.7/
git clone https://github.com/apache/incubator-toree.git
cd incubator-toree
make clean release APACHE_SPARK_VERSION=2.1.2
pip install --upgrade ./dist/toree-pip/toree-0.2.0.dev1.tar.gz
pip freeze |grep toree 
jupyter toree install --spark_home=$SPARK_HOME
```

To Start the notebook

```
$ SPARK_OPTS='--master=local[4]' jupyter notebook
```
