FROM python:3-alpine

WORKDIR /usr/src/app

RUN apk add --no-cache bash curl

COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

COPY main.py ./

CMD [ "python", "./main.py" ]
