import mne

# load the edf file
file_name = "sample_eeg_data.edf"
raw = mne.io.read_raw_edf(file_name, preload=True)

# extract the different bands

# delta
delta = raw.copy().filter(l_freq=0.5, h_freq=4)
#delta.plot_psd()

# theta
theta = raw.copy().filter(l_freq=4, h_freq=8)
#theta.plot_psd()

# alpha
alpha = raw.copy().filter(l_freq=8, h_freq=12)
#alpha.plot_psd()

# beta
beta = raw.copy().filter(l_freq=12, h_freq=30)
#beta.plot_psd()

# gamma
gamma = raw.copy().filter(l_freq=30, h_freq=100)
#gamma.plot_psd()

# plot the raw data
raw.plot(block=True)

# make sure the script doesn't exit
while True:
    pass


