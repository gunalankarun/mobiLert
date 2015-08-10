import numpy as np

def processFFT(byteMe):
    fftOut = np.fft.fft(byteMe, 32)
    Mean = fftOut[0]
    fftOut[0] = 0
    return (fftOut, Mean)

def func(a, b, c):
    mag = np.sqrt(np.absolute(a) ** 2 + np.absolute(b) ** 2 + np.absolute(c) ** 2)
    return mag

def get_FFT(x, y, z):
    arr_x = np.array(x).astype(np.float)
    arr_y = np.array(y).astype(np.float)
    arr_z = np.array(z).astype(np.float)
    sum2nd = 0
    dummyfftOut = func(processFFT(arr_x)[0], processFFT(arr_y)[0], processFFT(arr_z)[0])
    dummyMean = func(processFFT(arr_x)[1], processFFT(arr_y)[1], processFFT(arr_z)[1])
    x = type (dummyfftOut)

    sum2nd = np.sum(dummyfftOut[0:-1])
    return (dummyfftOut, dummyMean, sum2nd)
