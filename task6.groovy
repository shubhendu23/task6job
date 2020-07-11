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
        shell ('kubectl create -f /root/task6kube/pvc.yml ')
        shell (' sleep 60 ')
        shell ('if [[ $(ls | grep php) ]] ; then kubectl create -f /root/task6kube/deploymentphp.yml; else kubectl create -f /root/task6kube/deployment.yml; fi ')
        shell (' sleep 60 ')
        shell ('pod=$(sudo kubectl get --no-headers=true pods -o name | awk -F "/" "{print $2}") ',kubectl cp * $pod:/var/www/html/')
        //shell ('kubectl cp * $(kubectl get pod -l app=my-app -o jsonpath="{.items[0].metadata.name}"):/var/www/html/')     
        shell ('kubectl create -f /root/task6kube/service.yml ')
        }
        
    }
    
}
freeStyleJob('testing') {
    
    triggers {
        upstream('deployment launch', 'SUCCESS')
    }
    steps {
        
        shell ('status=$(curl -o /dev/null -s -w %{http_code} 192.168.99.100:30000)')
        shell ('if [[ $(curl -o /dev/null -s -w %{http_code} 192.168.99.100:30000) == 200 ]]; then exit 0; else exit 1; fi ')
    }
    
}
freeStyleJob('unstable notify') {
    
    triggers {
         upstream('testing', 'FAILURE')   
    }
    steps {
      shell ('python3 /root/task6kube/mail.py')
        
        
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
  
