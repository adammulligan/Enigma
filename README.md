# Prime Numbers and Cryptosystems

My final year project, Enigma, a study in to algorithms based around prime numbers and their uses. The application is a simple chat testbed that I use to test algorithms, and the report is a discussion of the engineering behind this and the algorithms.

Please read the report for an in-depth introduction and discussion.

Appendix D contains installation instructions. There is no need to compile the application yourself, however if you do wish to compile it, you may use the ant build file, found in `enigma/build.xml`.

Files are stored as so:

* `enigma/` - Source and compiled application.
* `latex_src/` - Source for the report.
* `Thesis.pdf` - The report.

# Licence

The entirety of this project -- this include all programming code, documentation, data, reports, latex source code, imagery, and any other files -- is released under the GNU Lesser General Public Licence version 3 (LGPLv3).

# GitHub Info

## Usage

If you are building from GitHub, run the following command:

`$ rake`

This will compile all the latex documents, compile and build a jar file and creates a neat distribution directory with all the things you would need to hand in a final year project on prime-number based cryptography.

If you are not building from GitHub, rake will not work and you will have to use ant to build the jar file.
