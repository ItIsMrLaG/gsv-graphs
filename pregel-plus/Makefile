CCOMPILE=mpic++
PREGEL_SYSTEM=/home/user1/github/PregelPlus/system
export LD_LIBRARY_PATH=$(HADOOP_HOME)/lib/native:$(JAVA_HOME)/lib/server

# JAVA 
export CLASSPATH:=$(shell $(HADOOP_HOME)/bin/hadoop classpath --glob)
#####

CPPFLAGS= -I$(HADOOP_HOME)/include -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux -I$(PREGEL_SYSTEM) -Wno-deprecated -O2
LIB = -L$(HADOOP_HOME)/lib/native
LDFLAGS = -lhdfs

run_%: %.elf
	mpiexec -n 8 $< # > $@.res  2> /dev/null

%.elf: %.cpp
	$(CCOMPILE) $< $(CPPFLAGS) $(LIB) $(LDFLAGS) -o $@

clean:
	-rm -f boruvka.elf msbfs.elf
