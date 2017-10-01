import jenkins.model.*

def jobName = System.getenv('SEEDJOB_NAME')
def gitUrl = System.getenv('SEEDJOB_GIT')
def gitCredId = System.getenv('SEEDJOB_SSH_ID')

def javaposse = '''
  <javaposse.jobdsl.plugin.ExecuteDslScripts>
    <targets>*.groovy</targets>
    <usingScriptText>false</usingScriptText>
    <scriptText></scriptText>
    <ignoreExisting>false</ignoreExisting>
    <removedJobAction>IGNORE</removedJobAction>
    <removedViewAction>IGNORE</removedViewAction>
    <lookupStrategy>JENKINS_ROOT</lookupStrategy>
    <additionalClasspath></additionalClasspath>
  </javaposse.jobdsl.plugin.ExecuteDslScripts>
'''.stripIndent().trim()

def configXml = """<?xml version='1.0' encoding='UTF-8'?>
  <project>
    <actions/>
    <description>Create Jenkins jobs from DSL groovy files</description>
    <keepDependencies>false</keepDependencies>

    <triggers class='vector'>
      <hudson.triggers.TimerTrigger>
        <spec>@hourly</spec>
      </hudson.triggers.TimerTrigger>
    </triggers>

    <scm class='hudson.plugins.git.GitSCM'>
      <configVersion>2</configVersion>
      <userRemoteConfigs>

        <hudson.plugins.git.UserRemoteConfig>
          <url><![CDATA[${gitUrl}]]></url>
          <credentialsId>${gitCredId}</credentialsId>
        </hudson.plugins.git.UserRemoteConfig>

      </userRemoteConfigs>
      <branches>

        <hudson.plugins.git.BranchSpec>
          <name>**</name>
        </hudson.plugins.git.BranchSpec>

      </branches>
      <doGenerateSubmoduleConfigurations>false</doGenerateSubmoduleConfigurations>
      <submoduleCfg class='list'/>
      <extensions/>
    </scm>

    <canRoam>true</canRoam>
    <disabled>false</disabled>
    <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>
    <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>

    <concurrentBuild>false</concurrentBuild>
    <builders>
      ${javaposse}
    </builders>

    <publishers/>
    <buildWrappers/>

  </project>
""".stripIndent().trim()

if (!Jenkins.instance.getItem(jobName)) {
  def xmlStream = new ByteArrayInputStream( configXml.getBytes() )
  try {
    def seedJob = Jenkins.instance.createProjectFromXML(jobName, xmlStream)
    seedJob.scheduleBuild(0, null)
  } catch (ex) {
    println "ERROR: ${ex}"
    println configXml.stripIndent()
  }
}