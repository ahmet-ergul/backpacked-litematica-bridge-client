import zipfile,sys
src,dst=sys.argv[1],sys.argv[2]
zi=zipfile.ZipFile(src); zo=zipfile.ZipFile(dst,'w',zipfile.ZIP_DEFLATED)
for n in zi.namelist():
    d=zi.read(n)
    if n.endswith('.class') and d[:4]==b'\xca\xfe\xba\xbe' and d[7]>65:
        d=d[:6]+b'\x00\x41'+d[8:]
    zo.writestr(n,d)
zo.close()
