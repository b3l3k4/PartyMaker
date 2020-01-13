from urllib.request import urlopen as uReq
import urllib.request
import threading
from bs4 import BeautifulSoup as soup
import time
import requests
from concurrent.futures import ThreadPoolExecutor
import asyncio
import firebase_admin
from firebase_admin import credentials
from firebase_admin import db
import uuid
import wget

cities = ['vilnius', 'kaunas', 'klaipeda', 'siauliai', 'utena', 'marijampole', 'panevezys', 'alytus', 'telsiai', 'taurage']

base = 'http://www.plotai.lt'

headers = {'User-Agent': 'Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:70.0) Gecko/20100101 Firefox/70.0'}

cred = credentials.Certificate('creds.json')
firebase_admin.initialize_app(cred, {
	'databaseURL' : 'https://party-maker-3b936.firebaseio.com/'
})

root = db.reference()

def get_links(city, session):
	response = session.get(f"{base}/{city}", headers=headers)

	print(response.status_code)
	if response.ok:
		html = response.text
		bs_html = soup(html, "html.parser")
		pages_container = bs_html.find("div",{"id":"pages"})
		urls = []

		if pages_container is not None:
			pages = pages_container.findAll("a",{"class":"num"})
			for x in range(1, int(pages[len(pages) - 1].text) + 1):
				urls.append(f"{base}/visi-tipai/{city}/page/{x}")
			return urls
		else:
			urls.append(f"{base}/visi-tipai/{city}/page/1")
			return urls
	else:
		print("something wrong")

async def get_links_async(cities):
	res = []
	with ThreadPoolExecutor(max_workers=4) as executor:
		with requests.Session() as session:
			loop = asyncio.get_event_loop()
			tasks = [
				loop.run_in_executor(executor, get_links, *(city, session)) for city in cities
			]
			for response in await asyncio.gather(*tasks):
				res.append(response)
	return res

def download_img(img_url, uid):
	path = './images/' + uid + '.jpg'

	local_image = wget.download(img_url, path)

def get_elements(url, session):

	response = session.get(url, headers=headers)

	elements = []

	response.encoding = response.apparent_encoding

	print(response.status_code, url)
	if response.ok:
		html = response.text
		bs_html = soup(html, "html.parser")

		#print(url, 'response is ok')

		adverts_container = bs_html.find("div",{"id":"skelbimai"})

		if adverts_container is not None:

			#print(url, 'adverts container exists')

			adverts = adverts_container.findAll("div",{"class":"col-lg-4 col-sm-6"})

			for advert in adverts:
				img_container = advert.find("div",{"class":"main-img-wrap"})
				image_url = "http://www.plotai.lt/" + img_container.img['src']

				link_container = advert.a
				link = link_container['href']

				title_container = advert.find("h3",{"class":"title"})
				title = title_container.text
				#print(title)

				category_container = advert.find("div",{"class":"category"})
				category = category_container.text

				# OPTIONAL VARIABLES
				price_container = advert.find("div",{"class":"price-all"})
				if price_container is not None:
					price = price_container.text
				else:
					price = "Nenurodyta"

				ppl_count_container = advert.find("div",{"class":"ppl-count"})
				if ppl_count_container is not None:
					ppl_count = ppl_count_container.text
				else:
					ppl_count = "Nenurodyta"

				randUUID = uuid.uuid1()

				download_img(image_url, str(randUUID))

				data = {
					'id':str(randUUID),
					'name':title,
					'category':category,
					'price':price,
					'max_capacity':ppl_count,
					'advert_url':link,
					'image_url':image_url
				}

				elements.append(data)
				root.child('Skelbimai').push(data)
			return elements
	else:
		print('Something wrong')

async def get_elements_async(urls, numOfLinks):
	res = []
	with ThreadPoolExecutor(max_workers=numOfLinks) as executor:
		with requests.Session() as session:
			loop = asyncio.get_event_loop()
			tasks = [
				loop.run_in_executor(executor, get_elements, *(url, session)) for url in urls
			]
			for response in await asyncio.gather(*tasks):
				res.append(response)
	return res

urls = []
start = time.time()
loop = asyncio.get_event_loop()
future = asyncio.ensure_future(get_links_async(cities))
res = loop.run_until_complete(future)
#print(res)
for x in range(0, len(res)):
	#print(len(res[x]))
	for item in res[x]:
		urls.append(item)

#print('------------------------------')
#print(flat_list)

flat_list_adverts = []

subcounter2 = 1;

print(urls);

loop = asyncio.get_event_loop()
future = asyncio.ensure_future(get_elements_async(urls, len(urls)))
res = loop.run_until_complete(future)

end = time.time()
elapsed = end - start;
print(f"async took {elapsed} seconds") #1.524864673614502 seconds
