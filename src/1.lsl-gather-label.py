import numpy as np
import os
from pylsl import StreamInlet, resolve_stream
from pynput import keyboard
import time
import mne

# Initialize global variables for key press
label = None
data_dir = 'test_data'
window_size = 256  # Must be a multiple of 4
epoch_size = 10

def on_press(key):
    global label
    try:
        if key.char == 'a':  # left arrow key
            label = 'left'
        elif key.char == 'd':  # right arrow key
            label = 'right'
    except AttributeError:
        pass

def on_release(key):
    global label
    label = 'neutral'

# Start a keyboard listener
listener = keyboard.Listener(on_press=on_press, on_release=on_release)
listener.start()

# Resolve an EEG stream on the lab network
print("Looking for an EEG stream...")
streams = resolve_stream('type', 'EEG')

# Create a new inlet to read from the stream
inlet = StreamInlet(streams[0])

# Ensure data directory exists
if not os.path.exists(data_dir):
    os.makedirs(data_dir)

# Initialize lists to hold data and labels
data_windows = []
label_windows = []

print("Collecting data...")
try:
    while True:
        # Collect 256 samples of data
        samples = []
        labels = []
        while len(samples) < window_size:
            sample, timestamp = inlet.pull_sample()
            if len(sample) == 2:  # Ensure we have 2 channels (FP1, FP2)
                samples.append(sample)
                labels.append(label if label is not None else 'neutral')
        
        # Convert to numpy array and apply STFT
        samples = np.array(samples).T
        stft_fp1 = mne.time_frequency.stft(samples[0], wsize=window_size, tstep=window_size//2)
        stft_fp2 = mne.time_frequency.stft(samples[1], wsize=window_size, tstep=window_size//2)

        # Take the magnitude of the STFT results
        magnitude_fp1 = np.abs(stft_fp1)
        magnitude_fp2 = np.abs(stft_fp2)

        # Flatten the magnitude results and append the label
        fft_window = np.concatenate([magnitude_fp1.flatten(), magnitude_fp2.flatten()])
        current_label = labels[0]  # Use the label at the start of the window
        
        # Append data and label to lists
        data_windows.append(fft_window)
        label_windows.append(current_label)
        
        # Save data in epochs of 10 windows
        if len(data_windows) == epoch_size:
            timestamp = int(time.time())
            epoch_data = np.array(data_windows)
            epoch_labels = np.array(label_windows)
            np.save(os.path.join(data_dir, f"{timestamp}-epoch-data.npy"), epoch_data)
            np.save(os.path.join(data_dir, f"{timestamp}-epoch-labels.npy"), epoch_labels)
            
            # Clear the lists for the next epoch
            data_windows = []
            label_windows = []
            print(f"Saved {timestamp}-epoch-data.npy and {timestamp}-epoch-labels.npy")

except KeyboardInterrupt:
    print("Data collection stopped")
