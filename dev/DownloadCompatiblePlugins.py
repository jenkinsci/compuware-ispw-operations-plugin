# -*- coding: utf-8 -*-
"""
Created on Mon Apr 21 15:07:54 2025

@author: Administrator
"""
import pandas as pd
import requests
dataset = pd.read_excel("List_of_plugins.xlsx", header=None)

destination = input("Where do you want the plugins to be downloaded? ")

list_of_urls = dataset.iloc[:, :].values.tolist()

for url_arr in list_of_urls:
    url = url_arr[0]
    filename_arr = url.split("/")
    length = len(filename_arr)
    filename = filename_arr[length-1].replace(".hpi",".jpi")
    
    print(f"{destination}\{filename}")
    try:
        dependency = requests.get(url, stream=True)
        with open(f"{destination}\{filename}", 'wb') as file:
            for chunk in dependency.iter_content(chunk_size=8192):
                file.write(chunk)
            print(f"Downloaded '{filename}' successfully.")
    except Exception as e:
        print(f"An unexpected error occurred while processing file : {filename} : {e}")