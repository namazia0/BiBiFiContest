JFLAGS = -g
JC = javac

CLASS_FILES = l_append/LogAppend.class l_read/LogRead.class

Default: $(CLASS_FILES) create_bin

%.class: %.java
	$(JC) $<

create_bin:
	jar -cvmf MANIFEST_LA.MF logappend.jar l_append/LogAppend.class Utils/*.class
	cat stub.sh logappend.jar > logappend && chmod +x logappend

	jar -cvmf MANIFEST_LR.MF logread.jar l_read/LogRead.class Utils/*.class l_read/SortById.class
	cat stub.sh logread.jar > logread && chmod +x logread

clean:
	$(RM) l_read/*.class l_append/*.class log* Utils/*.class l_read/SortById.class
