import jenkins

import xlsxwriter

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

            destination = input("Path to save list of plugins:")
            workbook = xlsxwriter.Workbook(f"{destination}/List_of_plugins.xlsx")
            worksheet = workbook.add_worksheet('Plugins')
            row = 0
            for plugin in server.get_plugins().values():
                download_url = f"https://updates.jenkins.io/download/plugins/{plugin['shortName']}/{plugin['version']}/{plugin['shortName']}.hpi"
                worksheet.write(row, 0, download_url)
                row = row + 1
                print(f"{download_url}")
            workbook.close()
        except Exception as e:
            print(f"An unexpected error occurred while fetching list of plugins from : {_self.jenkins_url} : {e}")

plugins = Plugins()
plugins.get_plugin_details()
print("Successfuly created List_of_plugins.xlsx !")