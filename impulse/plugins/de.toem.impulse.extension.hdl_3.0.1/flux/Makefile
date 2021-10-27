DIRS = fsdb fst wlf 
BUILD = fst
TRACE = $(DIRS)
all:
	for d in $(BUILD); do (cd $$d && $(MAKE) all); done       
        
clean:
	for d in $(DIRS); do (cd $$d && $(MAKE) clean ); done
	
trace:
	for d in $(TRACE); do (cd $$d && $(MAKE) trace ); done