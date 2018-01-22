import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import groovy.json.JsonSlurper

class GitCommitFetcher {
    RESTClient gitClient = new RESTClient('https://api.github.com')


    static void main(String[] args) {

        def repos = []
        repos = allCommitsPerRepository()

    }


    def static allCommitsPerRepository(){
        def TOKEN = '3ba9cd4770d32e8b02cd961c343f715ca972a249'
        def response
        def repos_list = []
        def i = 0
        def current_repository
        def commits_array_by_repo = []

        try {
            response = get_results_from_gitHub('user/repos', ['access_token':TOKEN, 'page': i])
            while (response.size() != 0){
                repos_list +=  retrieve_repositories_list(response)
                i++
                response = get_results_from_gitHub('user/repos', ['access_token':TOKEN, 'page': i])
            }
            for (i = 0; i < repos_list.size(); i++){
                current_repository = repos_list.get(i)
                response = get_results_from_gitHub('repos/heed-dev/' + current_repository + "/commits" , ['access_token':TOKEN, 'since':'2018-01-16T14:29:05Z'])
                commits_array_by_repo.add(current_repository)
                commits_array_by_repo[i] += retrieve_commits_array(response)
            }

        } catch (HttpResponseException e) {
            response = e.response
        }

    }

    def get_results_from_gitHub(String path, LinkedHashMap query ){
        def slurper = new JsonSlurper()
        def parsed
        def response
        response = gitClient.get(path: path, query: query)
        parsed = response.getData()
        return slurper.parseText(parsed.toString())

    }

    def static retrieve_repositories_list(List json ){
        def repos_list = []
        def i
        def size = json.size()
        for (i = 0; i < size; i++){
             repos_list.add(json.getAt(i).getAt('name'))
        }
        return repos_list

    }

    def static retrieve_commits_array(List json_commits){
        def commits_array = []
        def commits_size = json_commits.size()
        def i
        for (i = 0; i < commits_size; i++){
            commits_array.add([json_commits.getAt(i).getAt('sha'), json_commits.getAt(i).getAt('commit').getAt('author').getAt('name')])

        }
        return commits_array
    }

}

