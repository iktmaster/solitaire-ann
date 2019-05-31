import os

filenames = []

def addFiles():
    for i in range(1000):
        if i%1000==0: print(i)
        fileName = "./output" + str(i) + ".txt"
        if (os.path.isfile(fileName)):
            filenames.append(fileName)

print("Adding files...")
addFiles()

i = 0

print("Making dataset...")
with open('./dataset1k.txt', 'w') as outfile:
    for fname in filenames:
        i += 1
        if i%1000==0: print(i, len(filenames))
        with open(fname) as infile:
            for line_s in infile:
                line = line_s.split(" ")
                if (not line[-1].rstrip() == '-1'):
                    outfile.write(line_s)