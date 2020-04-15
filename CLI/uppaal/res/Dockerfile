FROM debian:stable

COPY ./bin-Linux/* ./
EXPOSE 2350
CMD ["./socketserver.sh","-s./server.sh"]
