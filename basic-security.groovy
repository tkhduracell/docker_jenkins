#!groovy

import jenkins.*
import jenkins.model.*

import hudson.*
import hudson.security.*
import hudson.plugins.*
import hudson.plugins.active_directory.*

import org.jenkinsci.main.modules.cli.auth.ssh.*

import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.common.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.plugins.credentials.impl.*
import com.cloudbees.plugins.credentials.impl.sshcredentials.impl.*

import com.cloudbees.jenkins.plugins.sshcredentials.impl.*

def jenkins = Jenkins.getInstance()

def myUser = System.getenv('SEEDJOB_INITIAL_USER')
def myPass = System.getenv('SEEDJOB_INITIAL_PASS')
def myKeyId = System.getenv('SEEDJOB_SSH_ID')
def myKeyUser = System.getenv('SEEDJOB_SSH_USER')
def mySshKey = System.getenv('SEEDJOB_SSH_SECRET')
def myKeyDesc = "inital ssh key"

if (!(jenkins.getSecurityRealm() instanceof HudsonPrivateSecurityRealm)) {
    jenkins.setSecurityRealm(new HudsonPrivateSecurityRealm(false))
}

if (!(jenkins.getAuthorizationStrategy() instanceof GlobalMatrixAuthorizationStrategy)) {
    jenkins.setAuthorizationStrategy(new GlobalMatrixAuthorizationStrategy())
}

// setup admin user
def currentUsers = jenkins.getSecurityRealm().getAllUsers().collect { it.getId() }
if (!(myUser in currentUsers)) {
    def user = jenkins.getSecurityRealm()
    	.createAccount(myUser, myPass)
    user.addProperty(new UserPropertyImpl(mySshKey))
    user.save()

    jenkins.getAuthorizationStrategy()
    	.add(Jenkins.ADMINISTER, myUser)
}
jenkins.save()

// setup SSH access
key = new BasicSSHUserPrivateKey.DirectEntryPrivateKeySource(mySshKey)
provider = 'com.cloudbees.plugins.credentials.SystemCredentialsProvider'
credentials = new BasicSSHUserPrivateKey(CredentialsScope.GLOBAL, myKeyId, myKeyUser, key, null, myKeyDesc)

credentials_store = Jenkins.instance.getExtensionList(provider)[0].getStore()
credentials_store.addCredentials(Domain.global(), credentials)


