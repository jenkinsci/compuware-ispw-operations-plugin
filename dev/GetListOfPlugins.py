import jenkins

from xlwt import Workbook 

class Plugins:
    def get_server_instance(_self):
        _self.jenkins_url = input("Enter Jenkins URL: ")
        username = input("Jenkins username: ")
        password = input("Jenkins password: ")
        server = jenkins.Jenkins(_self.jenkins_url, username=username, password=password)
        return server
    
    def get_plugin_details(_self):
        try:
            server = _self.get_server_instance()
            wb = Workbook() 
          
            #  add_sheet is used to create sheet. 
            sheet = wb.add_sheet('Plugins') 
            destination = input("Path to save list of plugins:")
            count = 0
            for plugin in server.get_plugins().values():
                download_url = f"https://updates.jenkins.io/download/plugins/{plugin['shortName']}/{plugin['version']}/{plugin['shortName']}.hpi"
                sheet.write(count, 0, download_url)
                count = count + 1
                print(f"{download_url}")
            
            wb.save(f"{destination}/list_of_plugins.xls")
        except Exception as e:
            print(f"An unexpected error occurred while fetching list of plugins from : {_self.jenkins_url} : {e}")

plugins = Plugins()
plugins.get_plugin_details()
print("Successfuly created list_of_plugins.xls !")