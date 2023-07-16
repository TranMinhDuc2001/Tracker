from Adafruit_IO import Client
import pyodbc
import time

def write_to_sql_server(data):
    #Connect to SQL Server
    connection_string = 'DRIVER={SQL Server};SERVER=[Your Server Name];DATABASE=[Your DB Name];UID=[Your UserID];PWD=[Your Password]'
    connection = pyodbc.connect(connection_string)
    cursor = connection.cursor()

    data1 = data.split(",")[0]
    data2 = data.split(",")[1]
    timestamp = data.split(",")[2]
    

    if(check_last_timestamp()!= timestamp):
        print(check_last_timestamp()+" "+timestamp)
        query = "INSERT INTO Data (Latitude, Longitude,Timestamp) VALUES (?, ?,?)"
        cursor.execute(query, (data1,data2,timestamp)) 
        

    # Save and Change then Close the connect
    connection.commit()
    connection.close()
    
    
    
def check_last_timestamp():
    connection_string = 'DRIVER={SQL Server};SERVER=[Your Server Name];DATABASE=[Your DB Name];UID=[Your UserID];PWD=[Your Password]'
    connection = pyodbc.connect(connection_string)
    cursor = connection.cursor()
    
    query = "select top 1 Timestamp from Data order by Timestamp desc"
    cursor.execute(query)
    rows = cursor.fetchall()
    result_string = ""

    for row in rows:
        result_string = ",".join([str(cell) for cell in row])
    return result_string
    
    
    
# Connect to Adafruit MQTT
ADAFRUIT_IO_USERNAME = 'ductran143'
ADAFRUIT_IO_KEY = 'aio_RZJk91JHEtENAre4WuApti2yrjo9'
aio = Client(ADAFRUIT_IO_USERNAME, ADAFRUIT_IO_KEY)

# Take data from adafruits and write to your DB
while(True):
    feed_name = 'device2'
    data = aio.receive(feed_name)
    write_to_sql_server(data.value)
