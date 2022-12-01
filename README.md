# SimQEcoIM
SimQEcoIM has been designed to overcome the difficulties associated with acquiring and operating physical energy measurement equipment in memory SSDs when processing applications.
## Data Storage Systems (DSS) installation (Hyrise)
To build and install Hyrise, please follow the official documentation: https://
github.com/hyrise/hyrise/wiki/Step-by-Step-Guide
## Some librairies
### Rjava:(https://cran.r-project.org/web/packages/rJava/index.html)

- Sudo apt-get install default-jdk
- Sudo R CMD javareconf
- Sudo apt-get install r-cran-rjava
- Sudo apt-get install libgdal-dev libproj-dev
- Install.packages("rJava")
- Location of "R Home"
- Appache netbeans.conf location and add export R HOME=path at the end of file
- Locate rjava path folder
- Add jar librairy in App

### Papi-java:(https://github.com/creichen/papi-java)

- ./configure {with-OS=bgp
- make
- make install
