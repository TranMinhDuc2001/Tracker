from Adafruit_IO import Client
import pyodbc
import time

def write_to_sql_server(data):
    # Kết nối tới SQL Server
    connection_string = 'DRIVER={SQL Server};SERVER=DESKTOP-T3OUPQ2\HARUUU;DATABASE=Tracking;UID=sa;PWD=1'
    connection = pyodbc.connect(connection_string)
    cursor = connection.cursor()

    data1 = data.split(",")[0]
    data2 = data.split(",")[1]
    timestamp = data.split(",")[2]
    
    
    # Thực hiện câu truy vấn để ghi dữ liệu vào SQL Server
    if(check_last_timestamp()!= timestamp):
        print(check_last_timestamp()+" "+timestamp)
        query = "INSERT INTO Data (Latitude, Longitude,Timestamp) VALUES (?, ?,?)"
        cursor.execute(query, (data1,data2,timestamp))  # Giả sử dữ liệu có 2 cột
        

    # Lưu thay đổi và đóng kết nối
    connection.commit()
    connection.close()
    
    
    
def check_last_timestamp():
    connection_string = 'DRIVER={SQL Server};SERVER=DESKTOP-T3OUPQ2\HARUUU;DATABASE=Tracking;UID=sa;PWD=1'
    connection = pyodbc.connect(connection_string)
    cursor = connection.cursor()
    
    query = "select top 1 Timestamp from Data order by Timestamp desc"
    cursor.execute(query)
    rows = cursor.fetchall()
    result_string = ""

    for row in rows:
        result_string = ",".join([str(cell) for cell in row])
    return result_string
    
    
    
# Kết nối tới Adafruit MQTT
ADAFRUIT_IO_USERNAME = 'ductran143'
ADAFRUIT_IO_KEY = 'aio_RZJk91JHEtENAre4WuApti2yrjo9'
aio = Client(ADAFRUIT_IO_USERNAME, ADAFRUIT_IO_KEY)

# Lấy dữ liệu từ Adafruit MQTT và ghi vào SQL Server
while(True):
    feed_name = 'device2'
    data = aio.receive(feed_name)
    write_to_sql_server(data.value)
