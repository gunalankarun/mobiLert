#!/usr/bin/python2.7
import numpy as np
from sklearn.decomposition.PCA import PCA
from sklearn.cluster.KMeans import KMeans

def processFFT(byteMe):
    fftData = np.fft.fft(byteMe, 32)
    Mean = fftData[0]

    return(fftData, Mean)

def mPipelineTrain(xP, yP, zP):
    pca = PCA(n_components=32)
    data = np.array([xP, yP, zP])
    pca.fit(data)
    tData = pca.transform(data)
    
    # Cluster the PCA results and then calculate the distance from the cluster centers

    kmeans = KMeans()
    kmeans.fit(tData)

    return (pca, kmeans)

