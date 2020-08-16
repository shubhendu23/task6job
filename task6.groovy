freeStyleJob('Job1 Deploy') {
     scm {
        git {
            remote {
                name('origin')
                url('https://github.com/aady14/task6web.git')
            }
        }
    triggers {
        
    }
    steps {
       
        shell ('kubectl create -f /root/task6kube/pvc.yml ')
        shell (' sleep 60 ')
        shell ('if [[ $(ls | grep php) ]] ; then kubectl create -f /root/task6kube/deployphp.yml; else kubectl create -f /root/task6kube/deploy.yml; fi ')
        shell (' sleep 60 ')
        shell ('bash /root/task6kube/copypage.sh')
        shell ('kubectl create -f /root/task6kube/service.yml ')
        }
        
    }
    
}
freeStyleJob('Job2 test') {
    
    triggers {
        upstream('Job1 Deploy', 'SUCCESS')
    }
    steps {
        
        shell ('status=$(curl -o /dev/null -s -w %{http_code} 192.168.99.100:30000)')
        shell ('if [[ $(curl -o /dev/null -s -w %{http_code} 192.168.99.100:30000) == 200 ]]; then exit 0; else exit 1; fi ')
    }
     publishers{
        downstreamParameterized {
            trigger( 'Job3 notify') {
                condition( 'FAILED')
                triggerWithNoParameters(triggerWithNoParameters = true)

            }
        }
    }
    
}
    

freeStyleJob('Job3 notify') {
    
   
    steps {
      shell ('python3 /root/task6kube/mail.py')
        
        
    }
    
}
buildPipelineView('Groovy task') {
    filterBuildQueue()
    filterExecutors()
    title('Groovy task')
    displayedBuilds(5)
    selectedJob('Job1 Deploy')
    alwaysAllowManualTrigger()
    showPipelineParameters()
    refreshFrequency(60)
}
//shubhenduSaurabh  
