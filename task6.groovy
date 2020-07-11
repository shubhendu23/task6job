freeStyleJob('deployment launch') {
     scm {
        git {
            remote {
                name('origin')
                url('https://github.com/shubhendu23/task6web.git')
            }
        }
    triggers {
        
    }
    steps {
        shell ('kubectl create -f pvc.yml ')
        shell ('kubectl create -f service.yml ')
        shell (' if [[ $(ls | grep php) ]] ; then kubectl create -f deploymentphp.yml; else kubectl create -f deployment.yml; fi ')
            
        }
        
    }
    
}
freeStyleJob('testing') {
    
    triggers {
        upstream('deployment launch', 'SUCCESS')
    }
    steps {
        
        shell ('status=$(curl -o /dev/null -s -w %{http_code} 192.168.99.100:30000)')
        shell ('if [[ $status == 200 ]]; then exit 0; else exit 1; fi ')
    }
    
}
freeStyleJob('unstable notify') {
    
    triggers {
         upstream('testing', 'FAILURE')   
    }
    steps {
      shell ('./mail.py')
        
        
    }
    
}
buildPipelineView('task6') {
    filterBuildQueue()
    filterExecutors()
    title('task6')
    displayedBuilds(5)
    selectedJob('deployment launch')
    alwaysAllowManualTrigger()
    showPipelineParameters()
    refreshFrequency(60)
}