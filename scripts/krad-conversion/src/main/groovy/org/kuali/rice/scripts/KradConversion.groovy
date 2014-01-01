/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 *  uses properties file to load settings, builds scaffolding for project and runs related conversion
 *
 *  script pulls an input and target directory
 *  target directory is wiped and a structure is setup based on a web application maven project
 *  using the struts-config.xml the file is parsed and processed into creating a basic web-overlay project
 *  so the generated code can be tested without mixing with existing source*/
import org.apache.commons.io.FilenameUtils
import org.apache.commons.lang.StringUtils
import org.kuali.rice.scripts.ConversionUtils
import org.kuali.rice.scripts.DictionaryConverter
import org.kuali.rice.scripts.ScaffoldGenerator
import org.kuali.rice.scripts.StrutsConverter
import org.kuali.rice.scripts.KimPermissionConverter

// load configuration file(s)
def configFilePath = "./src/main/resources/krad.conversion.properties"
def projectConfigFilePath = System.getProperty("alt.config.location")
def config = ConversionUtils.getConfig(configFilePath, projectConfigFilePath)


// load any config-specific members
def inputDir = FilenameUtils.normalize(config.input.dir, true)
def outputDir = FilenameUtils.normalize(config.output.dir, true)
def outputPathList = config.output.path.list

def outputResourceDir = outputDir + config.output.path.src.resources
def projectApp = config.project.app
def templateDir = config.script.path.template
def projectArtifact = config.project.artifact
def isWarProject = "war".equals(config.project.artifact.war)
def projectParent = config.project.parent
def dependencies = config.project.dependencies

def performDictionaryConversion = config.bool.script.performDictionaryConversion;
def performStrutsConversion = config.bool.script.performStrutsConversion;
def performKimPermissionConversion = config.bool.script.performKimPermissionConversion;
def copyWebXml = config.bool.script.copyWebXml;
def copyPortalTags = config.bool.script.copyPortalTags;
def includeRiceValidationTest = config.bool.script.includeRiceValidationTest;
def coreXmlFilePathList = config.map.scaffold.rdvconfig.additionalCorefiles;
def strutsSearchDirPath = config.input.dir + config.input.path.src.webapp
def ignoreStrutsPattern = config.pattern.script.ignoreStruts;

// setup all necessary classes
ScaffoldGenerator scaffold = new ScaffoldGenerator(config)
StrutsConverter struts = new StrutsConverter(config)
DictionaryConverter dictionary = new DictionaryConverter(config)
KimPermissionConverter kimConverter = new KimPermissionConverter(config)

if (StringUtils.isBlank(inputDir) || StringUtils.isBlank(outputDir)) {
    System.out.println "Error:\nplease configure your input and output directories before continuing\n\n";
    System.exit(1);
}

if (!performDictionaryConversion && !(performStrutsConversion && isWarProject) && !performKimPermissionConversion) {
    System.out.println "Error:\nall conversion bypassed; exiting\n\n";
    System.exit(1);
} else {
    ConversionUtils.buildDirectoryStructure(outputDir, outputPathList, true)
    ScaffoldGenerator.buildOverlayPom(outputDir, projectApp, projectArtifact, projectParent, dependencies, [])

    if (isWarProject && copyWebXml) {
        System.out.println "Copy web.xml"
        ScaffoldGenerator.copyWebXml(inputDir, outputDir)
    }

    if (isWarProject && copyPortalTags) {
        System.out.println "Copy portal tags"
        ScaffoldGenerator.copyPortalTags(inputDir, outputDir, projectApp);
    }

    if (performDictionaryConversion) {
        System.out.println "Perform dictionary conversion"
        dictionary.convertDataDictionaryFiles()
        def springBeansFileList = ConversionUtils.findFilesByPattern(outputResourceDir, ~/\.xml$/, ~/META-INF/)
        // TODO: build portal tag for all views generated by dictionary conversion
    }

    if (performKimPermissionConversion) {
        System.out.println "Permform KIM Permission conversion"
        kimConverter.convertKimPermissions()
    }
}



if (performStrutsConversion) {
    // confirm existence of strut-config files and begin processing
    def strutsConfigFiles;
    if (ignoreStrutsPattern) {
        strutsConfigFiles = ConversionUtils.findFilesByPattern(strutsSearchDirPath, /struts-.*?\.xml$/, ignoreStrutsPattern);
    } else {
        strutsConfigFiles = ConversionUtils.findFilesByPattern(strutsSearchDirPath, /struts-.*?\.xml$/);
    }
    System.out.println "Load struts-config.xml files for processing - dir: " + strutsSearchDirPath + " " + strutsConfigFiles?.size()
    // generate spring controllers and other classes
    System.out.println "Generating all necessary spring components (controllers, forms, views) from strutsConverter information"
    strutsConfigFiles.each { strutsConfigFile ->
        def strutsConfig = StrutsConverter.parseStrutsConfig(strutsConfigFile.path)
        struts.generateSpringComponents(strutsConfig)
        // TODO: change portal tag builder to build for all new controllers
        // scaffold.buildPortalTag(strutsConfig)
    }
}


// find all spring files and add to a rice validation test (good precursor test)
def springBeansFileList = ConversionUtils.findFilesByPattern(outputResourceDir, ~/\.xml$/, ~/META-INF/)
def springBeansFilePathList = []
springBeansFileList.each { file -> springBeansFilePathList << file.path }

// includes a spring validation test to allow for testing before running the server application
if (includeRiceValidationTest) {
    System.out.println "Generating spring validation test based on resulting output from conversion"
    scaffold.buildSpringBeansValidationTest(outputDir, springBeansFilePathList, coreXmlFilePathList);
}


System.out.println " -- Script Complete"
System.out.println " -- open directory " + outputDir
System.out.println " -- prep project -- mvn eclipse:clean eclipse:eclipse generate-resources "
System.out.println " -- if using eclipse add target/generate-resources directory as a referenced library (Configure -> Build Path -> Library -> Add Class Folder "
// end of script
