from keras.models import Sequential
from keras.layers import Dense
from keras.layers import Flatten
from keras.layers import Activation
import keras
import numpy as np
import solitaire

from sklearn.preprocessing import LabelBinarizer

from keras.callbacks import TensorBoard
tensorboard = TensorBoard(log_dir="./Graphs/InClass",write_graph=True)

import random

#--------------------------------
USE_SAVED_MODEL = False
model_fname = "model"
dataset = "dataset1k"
parts = 1
EPOCHS = 10
#--------------------------------

game = solitaire.Solitaire()
game.setup()

X_train = []
Y_train = []

movesList = open('./legalMoves.txt').read()
movesList = movesList.split("\n")
#print(movesList)

def getData(part):
    if (parts == 1):
        file = open('./datasets/'+dataset+'.txt')
    else:
        file = open('./datasets/'+dataset+'_'+str(part+1)+'.txt')
    file_read = file.read()
    lines = file_read.split("\n")
    print(len(lines))
    data = [[int(i) for i in line.split(" ") if i != ''] for line in lines[:-1]]
    file.close()
    return data

def fitDaModal(x, y):
    model.fit(
        np.array(x),
        np.array(y),
        epochs=EPOCHS,
        verbose=1,
        callbacks=[tensorboard])

def makeTrainingSet(data):
    for d in data[:-1]:
        xtrain = d[:-1]
        xtrain = [i+1 for i in xtrain]
        xtrain_categorical = keras.utils.to_categorical(xtrain, num_classes=54)
        xtrain_categorical = np.array(xtrain_categorical).flatten()
        ytrain = d[-1:]
        X_train.append(xtrain_categorical)
        Y_train.append(ytrain)


def loadModel(name):
    #  load json and create model
    from keras.models import model_from_json
    json_file = open(name+'.json', 'r')
    loaded_model_json = json_file.read()
    json_file.close()
    loaded_model = model_from_json(loaded_model_json)
    # load weights into new model
    loaded_model.load_weights(name+".h5")
    print("Loaded model from disk!")
    return loaded_model


def saveModel(model, name):
    model_json = model.to_json()
    with open(name+".json", "w") as json_file:
        json_file.write(model_json)
        # serialize weights to HDF5
        model.save_weights(name+".h5")
        print("Saved model to disk!")

def loadMoves():
    nparray = np.loadtxt('./'+model_fname+'_umoves.txt', dtype=str, delimiter=" ")
    return nparray.tolist()

def saveMoves(uni):
    np.savetxt('./'+model_fname+'_umoves.txt', uni, fmt="%s", delimiter=" ")

transformed_label = []

print("USE_SAVED_MODEL", USE_SAVED_MODEL)

model = Sequential()
model.add(Dense(3494, input_dim=6372, activation='relu'))
model.add(Dense(615, activation='softmax'))

model.compile(loss='sparse_categorical_crossentropy', optimizer='adam', metrics=['accuracy'])

if (USE_SAVED_MODEL):
    print("Loading weights...")
    model = loadModel(model_fname)
else:
    for i in range(5):
        for part in range(parts):
            X_train = []
            Y_train = []
            print("Running over dataset part", part+1, "/", parts)
            print("Grabbing data...")
            data = getData(part)
            print("Make training data...")
            makeTrainingSet(data)

            #transformed_label = keras.utils.to_categorical(Y_train, num_classes=615)

            print("Fitting the model...")
            fitDaModal(np.array(X_train), np.array(Y_train))
    saveModel(model, model_fname)

def run_prediction():
    times = 10
    correct = 0
    for i in range(times):
        game = new_game()
        predict(game)
        u = input()
        if u=="y": correct += 1
    print(correct, times, (correct/times)*100)

def new_game():
    game = solitaire.Solitaire()
    game.setup()
    return game

def predict(cur_game):
    state = cur_game.get_state()
    state_read = cur_game.get_state_readable()
    state_categorical = keras.utils.to_categorical(state, num_classes=54)
    state_categorical = np.array(state_categorical).flatten()
    prediction = model.predict(np.array([state_categorical]))
    predic = np.argmax(prediction)
    text = movesList[predic-1]
    print("I predicted move", text, "for state", state_read, "||", state)