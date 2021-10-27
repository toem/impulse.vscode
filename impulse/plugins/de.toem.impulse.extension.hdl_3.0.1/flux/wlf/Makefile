SOURCES?=main.c ../flux.c ../lz4.c ../fastlz.c
HEADER?=../flux.h  ../lz4.h  ../fastlz.h
DEFINES?=-D FLX_CONTROL -D FLX_COMPRESS -D FLX_STDIO -D FLX_ITEM_EXTENSION="flxbyte signalType;flxuint signalScale;void*varId;void*value;" -Wno-pointer-to-int-cast -Wno-int-to-pointer-cast
INCLUDES?=
LIBS?=-lwlf
OUT=wlf
COMPILER?=gcc
RELEASE?=0
ADD_FLAGS?=
ADD_LIBS?=
FORCE?=0

ifeq ($(RELEASE),1)
FLAGS?=-I .. -I . $(INCLUDES) $(ADD_FLAGS) $(DEFINES) 
else
FLAGS?=-I .. -I . $(INCLUDES) $(ADD_FLAGS) $(DEFINES) -g
endif

ifeq ($(FORCE),1)
.FORCE:

DEPEND?=$(SOURCES) $(HEADER) Makefile .FORCE
else
DEPEND?=$(SOURCES) $(HEADER) Makefile
endif

######################################################################################
ifeq ($(OS),Windows_NT)
	
all: win32-x86_64/$(OUT).exe win32-x86/$(OUT).exe 

win32-x86/$(OUT).exe: $(DEPEND)
	- mkdir win32-x86
	$(COMPILER) $(SOURCES) $(SOURCES_WIN32) $(FLAGS) $(FLAGS_WIN32) $(LIBS) $(LIBS_WIN32-X86) $(ADD_LIBS) -o win32-x86/$(OUT).exe	
	
win32-x86_64/$(OUT).exe: $(DEPEND)	
	- mkdir win32-x86_64
	$(COMPILER)  $(SOURCES) $(SOURCES_WIN32) $(FLAGS) $(FLAGS_WIN32) $(LIBS) $(LIBS_WIN32-X86_64) $(ADD_LIBS) -o win32-x86_64/$(OUT).exe
	
clean:
	- rm -rf win32-x86/$(OUT).exe	
	- rm -rf win32-x86_64/$(OUT).exe	
	
else
UNAME_S := $(shell uname -s)

######################################################################################
ifeq ($(UNAME_S),Darwin)

all: macosx-x86_64/$(OUT) macosx-x86/$(OUT)

macosx-x86/$(OUT): $(DEPEND)
	mkdir -p macosx-x86
	$(COMPILER) -m32 $(SOURCES) $(SOURCES_MACOSX) $(FLAGS) $(FLAGS_MACOSX) $(LIBS) $(LIBS_MACOSX-X86) $(ADD_LIBS) -o macosx-x86/$(OUT)
	chmod 777 macosx-x86/$(OUT)

macosx-x86_64/$(OUT): $(DEPEND)
	mkdir -p macosx-x86_64
	$(COMPILER) -m64 $(SOURCES) $(SOURCES_MACOSX) $(FLAGS) $(FLAGS_MACOSX) $(LIBS) $(LIBS_MACOSX-X86_64) $(ADD_LIBS) -o macosx-x86_64/$(OUT)	
	chmod 777 macosx-x86_64/$(OUT)
	
clean:
	rm -rf macosx-x86/$(OUT)	
	rm -rf macosx-x86_64/$(OUT)	

######################################################################################
else # Linux

all: linux-x86_64/$(OUT) linux-x86/$(OUT) win32-x86_64/$(OUT).exe win32-x86/$(OUT).exe 

linux-x86/$(OUT): $(DEPEND)
	mkdir -p linux-x86
	$(COMPILER) -m32  $(SOURCES) $(SOURCES_LINUX) $(FLAGS) $(FLAGS_LINUX) $(LIBS) $(LIBS_LINUX-X86) $(ADD_LIBS) -o linux-x86/$(OUT)
	chmod 777 linux-x86/$(OUT)

linux-x86_64/$(OUT): $(DEPEND)
	mkdir -p linux-x86_64
	$(COMPILER) -m64 $(SOURCES) $(SOURCES_LINUX) $(FLAGS) $(FLAGS_LINUX) $(LIBS) $(LIBS_LINUX-X86_64) $(ADD_LIBS) -o linux-x86_64/$(OUT)
	chmod 777 linux-x86_64/$(OUT)
			
win32-x86/$(OUT).exe: $(DEPEND)
	mkdir -p win32-x86
	i686-w64-mingw32-$(COMPILER) $(SOURCES) $(SOURCES_WIN32) $(FLAGS) $(FLAGS_WIN32) $(LIBS) $(LIBS_WIN32-X86) $(ADD_LIBS) -o win32-x86/$(OUT).exe	
	
win32-x86_64/$(OUT).exe: $(DEPEND)	
	mkdir -p win32-x86_64
	x86_64-w64-mingw32-$(COMPILER)  $(SOURCES) $(SOURCES_WIN32) $(FLAGS) $(FLAGS_WIN32) $(LIBS) $(LIBS_WIN32-X86_64) $(ADD_LIBS) -o win32-x86_64/$(OUT).exe
	
clean:
	rm -rf linux-x86/$(OUT)
	rm -rf linux-x86_64/$(OUT)
	rm -rf win32-x86/$(OUT).exe	
	rm -rf win32-x86_64/$(OUT).exe	
	
endif
endif

######################################################################################

cleanall:
	rm -rf linux-x86/$(OUT)
	rm -rf linux-x86_64/$(OUT)
	rm -rf win32-x86/$(OUT).exe	
	rm -rf win32-x86_64/$(OUT).exe
	rm -rf macosx-x86/$(OUT)	
	rm -rf macosx-x86_64/$(OUT)		
	
trace:		