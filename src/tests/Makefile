# modify the paths appropriately
JDKPATH = /usr/lib/jvm/java-15-openjdk
LIBPATH = .:..
CLASSPATH = $(LIBPATH)
BINPATH = $(JDKPATH)/bin
JAVAC = $(JDKPATH)/bin/javac -classpath $(CLASSPATH)
JAVA  = $(JDKPATH)/bin/java  -classpath $(CLASSPATH)


UserSkyline:UserSkyline.java
	$(JAVAC) TestDriver.java UserSkyline.java


userskyline: UserSkyline
	$(JAVA) tests.UserSkyline



# test skyline operator

Skyline:Skyline.java
	$(JAVAC) TestDriver.java Skyline.java

skyline: Skyline
	$(JAVA) tests.Skyline

# test Sort operator

SortPrefTest:SortPrefTest.java
	$(JAVAC) TestDriver.java SortPrefTest.java

sortpreftest: SortPrefTest
	$(JAVA) tests.SortPrefTest	

#test Sort Int operator	

SortPrefInt:SortPrefInt.java
	$(JAVAC) TestDriver.java SortPrefInt.java

sortprefint: SortPrefInt
	$(JAVA) tests.SortPrefInt	


# NSkyline: NestedLoopSkylineTest.java
# 	$(JAVAC) NestedLoopSkylineTest.java

# nestedskyline: NestedLoopSkylineTest
# 	$(JAVA) tests.NestedLoopSkylineTest	



# test buffer manager

BMTest:BMTest.java
	$(JAVAC) TestDriver.java BMTest.java

bmtest: BMTest
	$(JAVA) tests.BMTest

# test disk manager

DBTest:DBTest.java
	$(JAVAC) TestDriver.java DBTest.java

dbtest: DBTest
	$(JAVA) tests.DBTest

# test heapfile 

HFTest:HFTest.java
	$(JAVAC) TestDriver.java HFTest.java

hftest: HFTest
	$(JAVA) tests.HFTest

# test btree

BTTest:BTTest.java
	$(JAVAC) TestDriver.java BTTest.java

bttest: BTTest
	$(JAVA) tests.BTTest

# test index

IndexTest:IndexTest.java
	$(JAVAC) TestDriver.java IndexTest.java

indextest: IndexTest
	$(JAVA) tests.IndexTest

# test join

JoinTest:JoinTest.java
	$(JAVAC) TestDriver.java JoinTest.java

jointest: JoinTest
	$(JAVA) tests.JoinTest

# test sort

SortTest:SortTest.java
	$(JAVAC) TestDriver.java SortTest.java

sorttest: SortTest
	$(JAVA) tests.SortTest

# test sortMerge

SMJoin:SM_JoinTest.java
	$(JAVAC) SM_JoinTest.java TestDriver.java

sortmerge: SMJoin
	$(JAVA) tests.SM_JoinTest




