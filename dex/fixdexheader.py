import zlib
import hashlib
import ctypes

def int32_to_uint32(i):
    return ctypes.c_uint32(i).value


class dexfile:
    dex=0
    def __init__(self,filename):
        self.dex=open(filename,'rb')
    
    def calsignature(self):
        self.dex.seek(32)
        sigdata=self.dex.read()
        sha1 = hashlib.sha1()
        sha1.update(sigdata)

        print "signature",sha1.hexdigest(),type(sha1.hexdigest())

    def calchecksum(self):
        self.dex.seek(12)
        checkdata=self.dex.read()
        checksum=zlib.adler32(checkdata)

        print "checksum",hex(int32_to_uint32(checksum)),type(hex(int32_to_uint32(checksum)))

print "old"
olddex=dexfile("testclasses.dex")
olddex.calsignature()
olddex.calchecksum()

print "new"
newdex=dexfile("classes_tmp.dex")
newdex.calsignature()
newdex.calchecksum()