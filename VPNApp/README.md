Add below dependencies in App level build.gradle

implementation project(':VPNAzure')

Add below dependencies in Project level setting.gradle

include ':VPNAzure'
project(':VPNAzure').projectDir = new File('../VPNAzure')