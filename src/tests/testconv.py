import matplotlib.pyplot as plt
import numpy as np
import mne
from scipy.ndimage import gaussian_filter1d

log_output = False

def log(*args):
    if log_output:
        print(*args)



def conversion_method(byte_string):
    log("\n|---------------")
    log("|-- ",str(byte_string))
    byte_array = bytes.fromhex(byte_string)
    log("|-- ",str(byte_array))
    raw_samples = [b & 0xFF for b in byte_array]
    log("|-- ",str(raw_samples))
    raw_samples[:] = raw_samples[2:20]
    log("|-- ",str(raw_samples))
    Sample1 = [0, 0]
    Sample2 = [0, 0]
    Sample3 = [0, 0]
    skip_bit = 0
    i = 0
    while skip_bit < 18:
        log("    |-----------")
        log("    | skip_bit: "+str(skip_bit))
        for j in range(2):
            log("        |-----------")
            log("        | j: "+str(j))
            temp_sample = [0, 0, 0]
            for loop in range(3):
                temp_sample[loop] = raw_samples[skip_bit + loop + j * 3]
            log("        | temp_sample: "+str(temp_sample))
            
            val1 = temp_sample[0] & 0xFF
            log("        | val1: "+str(val1))
            val2 = temp_sample[1] & 0xFF
            log("        | val2: "+str(val2))
            val3 = temp_sample[2] & 0xFF
            log("        | val3: "+str(val3))
            sample_value = (val1 << 16) + (val2 << 8) + val3

            log(f"        | sample_val steps: a: {str(val1<<16)} b: {str(val2<<8)} c: {str(val3)}")
            log("        | sample_value: "+str(sample_value))
            
            if i == 0:
                Sample1[j] = sample_value #& 0xFF
            elif i == 1:
                Sample2[j] = sample_value #& 0xFF
            elif i == 2:
                Sample3[j] = sample_value #& 0xFF

        skip_bit += 6
        i += 1
    log("|-- Sample1: "+str(Sample1))
    log("|-- Sample2: "+str(Sample2))
    log("|-- Sample3: "+str(Sample3))

    
    final_sample1 = multiplier(Sample1)
    final_sample2 = multiplier(Sample2)
    final_sample3 = multiplier(Sample3)
    return final_sample1, final_sample2, final_sample3

def multiplier(sample):
    final_sample = [0] * len(sample)
    for i in range(len(sample)):
        final_sample[i] = sample[i] * 0.4 / ((2 ** 23) - 1) #* 1000000.0
    return final_sample

all_samples = []
# Example usage:
with open("outputs/received_data.hex", "r") as f:
    raw_samples = f.read()
    # iterate over each row
    for row in raw_samples.split("\n"):
        result1, result2, result3 = conversion_method(row)
        #print(f"result1: {result1} result2: {result2} result3: {result3}")
        # take mean
        #print(f"result1: {result1} result2: {result2} result3: {result3}")
        #ch1_mean = np.mean([result1[0],result2[0],result3[0]])
        #ch2_mean = np.mean([result1[1],result2[1],result3[1]])
        #print(f"ch1_mean: {ch1_mean} ch2_mean: {ch2_mean}")
        #all_samples.append([ch1_mean, ch2_mean])

        all_samples.append(result1)
        all_samples.append(result2)
        all_samples.append(result3)



# plot the data using matplotlib, where all_samples is a list of lists containing two channels of data

x = [i for i in range(len(all_samples))]

ch1 = [i[0] for i in all_samples]
ch2 = [i[1] for i in all_samples]

# denoise the data

ch1 = gaussian_filter1d(ch1, sigma=2)
ch2 = gaussian_filter1d(ch2, sigma=2)

# show the data in mne

tentwenty = mne.channels.make_standard_montage("standard_1020")
tentwenty.plot()

# create the info structure needed by mne
info = mne.create_info(ch_names=['Fp1', 'Fp2'], sfreq=250, ch_types=['eeg', 'eeg'],)
# transpose the data so that it is in the correct format
data = np.array([ch1, ch2])

# create the raw object
raw = mne.io.RawArray(data, info)

raw.set_montage(tentwenty)

# filter the data (notch at 50, low pass at 100, high pass at 0.5)
raw.filter(5, 100)
raw.notch_filter(50)

# define frequency ranges for the power bands
freq_ranges = {"delta": [0.5, 4], "theta": [4, 8], "alpha": [8, 12], "beta": [12, 30], "gamma": [30, 100]}

# extract the different bands for both channels (fp1 and fp2)
delta = raw.copy().filter(l_freq=freq_ranges["delta"][0], h_freq=freq_ranges["delta"][1])
theta = raw.copy().filter(l_freq=freq_ranges["theta"][0], h_freq=freq_ranges["theta"][1])
alpha = raw.copy().filter(l_freq=freq_ranges["alpha"][0], h_freq=freq_ranges["alpha"][1])
beta = raw.copy().filter(l_freq=freq_ranges["beta"][0], h_freq=freq_ranges["beta"][1])
gamma = raw.copy().filter(l_freq=freq_ranges["gamma"][0], h_freq=freq_ranges["gamma"][1])

# change x to be in seconds
x = [i/250 for i in range(len(all_samples))]

fig, axs = plt.subplots(5)
fig.suptitle('channel 1 and channel 2 (raw and corrected)')
axs[0].plot(x, delta.get_data()[0], color="red")
axs[0].plot(x, delta.get_data()[1], color="blue")
axs[1].plot(x, theta.get_data()[0], color="red")
axs[1].plot(x, theta.get_data()[1], color="blue")
axs[2].plot(x, alpha.get_data()[0], color="red")
axs[2].plot(x, alpha.get_data()[1], color="blue")
axs[3].plot(x, beta.get_data()[0], color="red")
axs[3].plot(x, beta.get_data()[1], color="blue")
axs[4].plot(x, gamma.get_data()[0], color="red")
axs[4].plot(x, gamma.get_data()[1], color="blue")
axs[0].set_title("delta")
axs[1].set_title("theta")
axs[2].set_title("alpha")
axs[3].set_title("beta")
axs[4].set_title("gamma")

# create another plot for the raw data inside the same figure

fig, axs = plt.subplots(2)
fig.suptitle('channel 1 and channel 2 (raw)')
axs[0].plot(x, raw.get_data()[0], color="red")
axs[0].plot(x, raw.get_data()[1], color="blue")

plt.show()

# plot the fourier transform over time (frame of 1 second / 250samples) for all the bands for both channels (fp1 as red and fp2 as blue) below each other in a single plot. the two channels must be different colors, they should be overlayed on top of each other
#fig, axs = plt.subplots(5)
#fig.suptitle('channel 1 and channel 2')
#axs[0].plot(x, delta.get_data()[0], color="red")











## save to 2 csv files, one for each channel where the first column is the x axis and the second column is the channel data and the third axis is the hex representation as a 3 byte hex string
#
#with open("ch1.csv", "w") as f:
#    for i in range(len(x)):
#        f.write(f"{x[i]},{ch1[i]},{hex(int(ch1[i] * 1000000.0 / 0.4))}\n")
#
#with open("ch2.csv", "w") as f:
#    for i in range(len(x)):
#        f.write(f"{x[i]},{ch2[i]},{hex(int(ch2[i] * 1000000.0 / 0.4))}\n")

# save the data as a 2 channel wav file
#
#import wave
#import struct
#
## open the file for writing.
#wf = wave.open('test.wav', 'wb')
## set the channels
#wf.setnchannels(2)
## set the sample format
#wf.setsampwidth(2)
## set the sample rate to 250hz
#wf.setframerate(250)
## write the frames as integer (same format as original)
#for i in range(len(x)):
#    wf.writeframes(struct.pack('h', int(ch1[i]*100)))
#    wf.writeframes(struct.pack('h', int(ch2[i]*100)))
#
## close the file
#wf.close()

#print(f"x: {len(x)}")
#print(f"ch1: {np.mean(ch1)}")
#print(f"ch2: {np.mean(ch2)}")
#
#fig, axs = plt.subplots(2)
#fig.suptitle('channel 1 and channel 2')
#axs[0].plot(x, ch1)
#axs[1].plot(x, ch2)
#
#plt.show()

        
