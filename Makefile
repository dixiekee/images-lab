# Major Makefile variables.
# - CEPACKAGES lists the names of the principal distributed filesystem
#   packages. These are the packages that would be distributed to users. Test
#   cases and build tools are not included.
# - JARFILE is the name of the monolithic jar file capable of running both
#   storage and naming servers, and several client applications. A monolithic
#   jar file is not the only way to distribute components of the filesystem, but
#   it is used here for convenience.
# - ARCHIVE is the name of the zip archive created by the archive target for
#   source code submission and distribution.
# - JAVAFILES is all of the Java files in the project, including test cases and
#   build tools.

CEPACKAGES = contrast driver image transform
JARFILE = contrast.jar
ARCHIVE = project4.zip
JAVAFILES = */*.java

# Testing variables
# - INDIR is the directory containing the sample images to enhance.
# - OUTDIR is the directory that will contain the enhanced images. It should not
#   exist prior to running the driver.
INDIR = test.images
OUTDIR = out.images

# Define the variable CPSEPARATOR, the classpath separator character. This is
# : on Unix-like systems and ; on Windows. The separator is returned by a
# Java program implemented in build/PathSeparator.java. The Makefile fragment
# included here is made to depend on build/PathSeparator.class to ensure that
# the program is compiled before make procedes past this line.

include build/Makefile.separator

# Source and class directory tree bases. These are given as the classpath
# argument when running unit test and as the sourcepath argument when generating
# Javadoc for all files (including unit tests). The value is quoted for Cygwin:
# the Windows Java implementation requires the path separator to be ; but
# Cygwin's bash interprets this as a separator between commands.

UNITCLASSPATH = ".$(CPSEPARATOR)unit"

# Create the single monolithic jar file.
.PHONY : jar
jar : all-classes
	jar cfe $(JARFILE) driver.Driver \
		$(foreach package,$(CEPACKAGES),$(package)/*.class)

# Compile all Java files.
.PHONY : all-classes
all-classes :
	javac -cp $(JAVAFILES)

# Run the driver on the test images included
.PHONY : test
test : all-classes
	java driver.Driver $(INDIR) $(OUTDIR)

# Delete all intermediate and final output and leave only the source.
.PHONY : clean
clean :
	rm -rf $(JAVAFILES:.java=.class) $(ARCHIVE) $(JARFILE) $(OUTDIR)

# Create a source code archive.
.PHONY : archive
archive : clean
	zip -9r $(ARCHIVE) *

# Dependencies for the Makefile fragment reporting the classpath separator.
build/Makefile.separator : build/PathSeparator.class

build/PathSeparator.class : build/PathSeparator.java
	javac build/PathSeparator.java
