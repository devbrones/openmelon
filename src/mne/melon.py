import numpy as np
import mne
import matplotlib.pyplot as plt
import matplotlib
from pylsl import StreamInlet, resolve_stream

# Resolve the stream and create an inlet
stream_name = 'MelonEEG_Stream'  # Replace with your stream name
streams = resolve_stream('name', stream_name)
inlet = StreamInlet(streams[0])

# Select channels of interest (assuming Fp1 and Fp2)
channels = ['Fp1', 'Fp2']
matplotlib.use('TkAgg')
plt.style.use("https://raw.githubusercontent.com/dracula/matplotlib/master/dracula.mplstyle")
# Create a figure and axis for plotting wavelets
fig, axs = plt.subplots(5)
plt.ion()  # Turn on interactive mode for real-time plotting
fig.suptitle('Channel 1 and Channel 2 (Raw and Corrected)')
fig.show()


# create a figure and axis for plotting the fourier transform (for both channels)
fig2, axs2 = plt.subplots(1)
plt.ion()  # Turn on interactive mode for real-time plotting
fig2.suptitle('Channel 1 and Channel 2 (Fourier Transform)')
fig2.show()

# create a figure and axis for plotting artifacts
fig3, axs3 = plt.subplots(1)
plt.ion()  # Turn on interactive mode for real-time plotting
fig3.suptitle('Artifacts')
fig3.show()


# Buffer length for displaying the last 4 seconds of data (assuming 250 samples per second)
buffer_length = 250 * 4 
raw_buffer = []

timestamps_buffer = []  # Buffer to store timestamps

freq_ranges = {
    "delta": [0.5, 4], 
    "theta": [4, 8], 
    "alpha": [8, 12], 
    "beta": [12, 30], 
    "gamma": [30, 100]
}


# Infinite loop for real-time data processing and plotting
while True:
    # Read a chunk of data from the LSL stream
    chunk, timestamps = inlet.pull_chunk(timeout=1, max_samples=250)

    if chunk:
        # Convert the chunk into a numpy array
        chunk_np = np.array(chunk)

        # Append the chunk to the buffer
        raw_buffer.extend(chunk_np)

        # Trim buffer to display only the last 10 seconds of data
        if len(raw_buffer) > buffer_length:
            raw_buffer = raw_buffer[-buffer_length:]

        # Store timestamps in the timestamps buffer
        timestamps_buffer.extend(timestamps)

        # Trim timestamps buffer to match data buffer length
        if len(timestamps_buffer) > len(raw_buffer):
            timestamps_buffer = timestamps_buffer[-len(raw_buffer):]

        # Assuming the incoming data is in a matrix format [samples, channels]
        data = np.array(raw_buffer)

        # Create a raw object from the incoming data
        info = mne.create_info(ch_names=channels, sfreq=250, ch_types='eeg')
        raw = mne.io.RawArray(data.T, info)

        # Apply filters to the raw data
        raw.notch_filter(50)  # Notch filter at 50 Hz
        raw.notch_filter(100)  # Notch filter at 100 Hz (second harmonic)
        raw.filter(0.5, 100)  # Bandpass filter from 0.5 to 100 Hz

        # Set up artifact detection for eye movement and blinks. Then add markers to the plot
        eog_events = mne.preprocessing.find_eog_events(raw)
        n_blinks = len(eog_events)
        onsets = eog_events[:, 0] / raw.info['sfreq'] - 0.25
        durations = np.repeat(0.5, n_blinks)
        descriptions = ['blink'] * n_blinks
        blink_annot = mne.Annotations(onsets, durations, descriptions, orig_time=raw.info['meas_date'])
        raw.set_annotations(blink_annot)
        axs3.clear() # clear the axes
        raw.plot(axes=axs3, show=False, events=eog_events, start=0, duration=10, color='white', event_color='red')
        fig3.canvas.draw()
        fig3.canvas.flush_events()



        # Convert x-axis to seconds
        x = np.array(timestamps_buffer) / 250

        # clear the axes
        axs2.clear()
        # plot the fourier transform for both channels
        raw.compute_psd().plot(axes=axs2, show=False, color='white')
        fig2.canvas.draw()
        fig2.canvas.flush_events()

        # Extract different frequency bands for both channels        
        delta = raw.copy().filter(
            l_freq=freq_ranges["delta"][0], 
            h_freq=freq_ranges["delta"][1])
        
        theta = raw.copy().filter(
            l_freq=freq_ranges["theta"][0], 
            h_freq=freq_ranges["theta"][1])
        
        alpha = raw.copy().filter(
            l_freq=freq_ranges["alpha"][0], 
            h_freq=freq_ranges["alpha"][1])
        
        beta = raw.copy().filter(
            l_freq=freq_ranges["beta"][0], 
            h_freq=freq_ranges["beta"][1])
        
        gamma = raw.copy().filter(
            l_freq=freq_ranges["gamma"][0], 
            h_freq=freq_ranges["gamma"][1])

        # Plotting the different frequency bands for both channels
        axs[0].plot(x, delta.get_data()[0], color="hotpink")
        axs[0].plot(x, delta.get_data()[1], color="cyan")
        axs[1].plot(x, theta.get_data()[0], color="hotpink")
        axs[1].plot(x, theta.get_data()[1], color="cyan")
        axs[2].plot(x, alpha.get_data()[0], color="hotpink")
        axs[2].plot(x, alpha.get_data()[1], color="cyan")
        axs[3].plot(x, beta.get_data()[0], color="hotpink")
        axs[3].plot(x, beta.get_data()[1], color="cyan")
        axs[4].plot(x, gamma.get_data()[0], color="hotpink")
        axs[4].plot(x, gamma.get_data()[1], color="cyan")

        axs[0].set_title("Delta")
        axs[1].set_title("Theta")
        axs[2].set_title("Alpha")
        axs[3].set_title("Beta")
        axs[4].set_title("Gamma")
        
        # Set the x-axis limits to display only the last 10 seconds of data
        for ax in axs:
            ax.set_xlim(x[-1] - 0.1
                        if x[-1] > 0.1 else 0, x[-1])
            
        # reset the y-axis limits
        for ax in axs:
            ax.set_ylim(-1e-5, 1e-5)
        


        # hotpinkraw the plot without blocking
        fig.canvas.draw()
        fig.canvas.flush_events()