plugins {
    id("com.gradle.enterprise") version "3.17.2"
    id("org.danilopianini.gradle-pre-commit-git-hooks") version "2.0.4"
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
        publishOnFailure()
    }
}

gitHooks {
    commitMsg { conventionalCommits() }
    preCommit {
        tasks("ktlintCheck", "detekt")
    }
    createHooks(overwriteExisting = true)
}

rootProject.name = "publish-on-central"
