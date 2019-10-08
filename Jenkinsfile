pipeline {
    agent none
    tools {
    }
    environment {
    }
    stages {
        stage('Commit Stage') {
            steps {
                script {
                    echo "This is a dummy stage!"
                    sleep 5
                }
            }
        }
        stage('Deploy to Test') {
            steps {
                script {
                    echo "This is a dummy stage!"
                    sleep 5
                }
            }
        }
        stage('Running Hackathon Tests') {
            agent {
                label 'spectrum-docker'
            }
            environment {
                DISPLAY=":99.0"
            }
            steps {
                script {
                    sh '/etc/init.d/xvfb start'
                    sh 'gradle test allureReport'
                    sh '/etc/init.d/xvfb stop'
                }
            }
            post {
                always {
                    allure includeProperties: false, jdk: '', results: [[path: 'build/allure-results']]
                    junit (
                            testResults: 'build/test-results/test/*.xml',
                            testDataPublishers: [
                                    jiraTestResultReporter(
                                            configs: [
                                                    jiraStringField(fieldKey: 'summary', value: '${DEFAULT_SUMMARY}'),
                                                    jiraStringField(fieldKey: 'description', value: '${DEFAULT_DESCRIPTION}'),
                                                    jiraStringArrayField(fieldKey: 'labels', values: [jiraArrayEntry(value: 'Jenkins'), jiraArrayEntry(value:'Integration')])
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