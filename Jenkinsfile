pipeline {
    agent none
    stages {
        stage('Commit Stage') {
            steps {
                script {
                    echo "This is a dummy stage!"
                }
            }
        }
        stage('Deploy to Test') {
            agent {
                label 'azureserver'
            }
            steps {
                git 'https://github.com/alikayim/magento.git'
                script {
                    sh """
                    docker-compose stop
                    ls -lrt
                    pwd
                    docker-compose up -d
                    sleep 5
                    docker exec spectrum-pipeline_master_web_1 install-magento
                    docker exec spectrum-pipeline_master_web_1 install-sampledata
                    """
                }
            }
        }
        stage('Hackathon Acceptances Tests') {
            parallel {
                stage('Chrome Tests') {
                    agent {
                        label 'spectrum-docker'
                    }
                    steps {
                        script {
                            sh 'gradle -Dheadless=true test allureReport'
                        }
                    }
                    post {
                        always {
                            allure includeProperties: false, jdk: '', results: [[path: 'build/allure-results']]
                            junit(
                                    testResults: 'build/test-results/test/*.xml',
                                    testDataPublishers: [
                                            jiraTestResultReporter(
                                                    configs: [
                                                            jiraStringField(fieldKey: 'summary', value: '${DEFAULT_SUMMARY}'),
                                                            jiraStringField(fieldKey: 'description', value: '${DEFAULT_DESCRIPTION}'),
                                                            jiraStringArrayField(fieldKey: 'labels', values: [jiraArrayEntry(value: 'Jenkins'), jiraArrayEntry(value: 'Integration')])
                                                    ],
                                                    projectKey: 'TEST',
                                                    issueType: '10001',
                                                    autoRaiseIssue: true,
                                                    autoResolveIssue: true,
                                                    autoUnlinkIssue: false,
                                            )
                                    ]
                            )
                        }
                    }
                }
                stage('Firefox Tests') {
                    agent {
                        label 'spectrum-docker2'
                    }
                    steps {
                        script {
                            sh 'gradle -Dheadless=true test allureReport'
                        }
                    }
                }
            }
        }
        stage('Deploy to Production') {
            steps {
                script {
                    echo "This is a dummy stage!"
                    sleep 5
                }
            }
        }
    }
}
