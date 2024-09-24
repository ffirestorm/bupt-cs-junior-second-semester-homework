from pyhive import hive
import matplotlib.pyplot as plt

plt.rcParams['font.sans-serif'] = ['SimHei']  # 步骤一(替换 sans-serif 字体)
plt.rcParams['axes.unicode_minus'] = False  # 步骤二(解决坐标轴负数的负号显示问题)
conn = hive.Connection(host='node1ip',
                       port=10000,
                       auth='NOSASL',
                       username='root')  # TODO: host值改为node1公网ip
cursor = conn.cursor()
cursor.execute(
    'SELECT keyword,count(*) as cnt FROM sogou_100w.sogou_ext_20111230 GROUP BY keyword order by cnt desc limit 10'
)
keywords = []
frequency = []
for result in cursor.fetchall():
    keywords.append(result[0])
    frequency.append(result[1])
cursor.close()
conn.close()
plt.barh(keywords, frequency)
plt.title('频度排名-学号')  # TODO: 学号替换为你的学号
plt.xlabel('频度')
plt.ylabel('关键词')
plt.show()
