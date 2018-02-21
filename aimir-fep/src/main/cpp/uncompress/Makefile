###########################################################################
##
## Copyright (c) 2004 NURI-Telecom Co., Ltd. 
## All rights reserved. 
##
## Redistribution and use in source and binary forms, with or without 
## modification, are permitted provided that the following conditions are met: 
##
## * Redistributions of source code must retain the above copyright notice, 
## this list of conditions and the following disclaimer. 
## * Redistributions in binary form must reproduce the above copyright notice, 
## this list of conditions and the following disclaimer in the documentation 
## and/or other materials provided with the distribution. 
## * Neither name of Intel Corporation nor the names of its contributors 
## may be used to endorse or promote products derived from this software 
## without specific prior written permission.
## 
## THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
## ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
## LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR 
## A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL INTEL OR 
## CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
## EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
## PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
## PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY 
## OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
## NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
## SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
##
###########################################################################

include Makefile.rules

##################################################################
#
# 설치시에는 다음의 디렉토리를 변경하여야 합니다.
#
# INCDIR (zlib가 있는 include 디렉토리)
# LIBDIR (zlib가 있는 lib 디렉토리)
#
##################################################################

INCDIR		=zlib
LIBDIR		=zlib

TARGET      =./uncompress
INCLUDES    =-I.
INCLUDES   +=-I$(INCDIR)
CFLAGS     +=-Wall -O2
LDFLAGS     =
LIBS		=-lstdc++ -lpthread

SOURCES = uncompress.cpp

OBJECTS = $(SOURCES:.cpp=.o)	\
		$(LIBDIR)/libz$(CROSS_NAME).a

all: $(TARGET)
package: $(TARGET)

$(TARGET): $(SOURCES) $(OBJECTS)
	$(CC) $(CFLAGS) $(OBJECTS) $(LIB_PATH) $(LIBS) -o $@ 

%.o:	%.cpp
	$(CPP) $(CFLAGS) $(INCLUDES) -c $<

clean:
	@rm -f $(TARGET)
	@rm -f *.o
	@rm -f *.exe

