CCOMPILE=mpic++
PP_HOME?=/home/user1/github/PregelPlus
PREGEL_SYSTEM=$(PP_HOME)/system
export LD_LIBRARY_PATH=$(HADOOP_HOME)/lib/native:$(JAVA_HOME)/lib/server
PROC_NUM ?= 8

# JAVA 
export CLASSPATH:=$(shell $(HADOOP_HOME)/bin/hadoop classpath --glob)
#####

CPPFLAGS= -I$(HADOOP_HOME)/include -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux -I$(PREGEL_SYSTEM) -Wno-deprecated -O2
LIB = -L$(HADOOP_HOME)/lib/native
LDFLAGS = -lhdfs

all: boruvka.elf msbfs.elf
	@echo "🛠️🔨 YEAH, ALL IS FORGED 🛠️🔨"

run_%: %.elf
	mpiexec -n $(PROC_NUM) $(PWD)/$< > $@.res  2> /dev/null

%.elf: %.cpp
	$(CCOMPILE) $< $(CPPFLAGS) $(LIB) $(LDFLAGS) -o $@


clean:
	-rm -f boruvka.elf msbfs.elf
