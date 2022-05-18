# Librosa Defaults

## mfcc
https://librosa.org/doc/0.9.1/generated/librosa.feature.mfcc.html?highlight=mfcc#librosa.feature.mfcc

| Name | Parameter | Default Value | 
| --- | --- | --- | --- |
|Data | y | None |
| Sample Rate| sr | 22050|
| log-power Mel spectrogram | S | None |
| Number of mfccs| n_mfcc | 20 (We use 40 in training)|
| Discrete cosine transform (DCT) type. By default, DCT type-2 is used| dct_type | 2 |
| Normalization | norm | 'ortho' |
| Liftering  | lifter | 0 |

## Mel Spectogram
https://librosa.org/doc/0.9.1/generated/librosa.feature.melspectrogram.html#librosa.feature.melspectrogram

| Name | Parameter | Default Value | 
| --- | --- | --- | --- |
| Data| y | None |
| Sample Rate | sr | 22050|
| Spectogra | S | None |
| length FFT Window | n_fft | 2048 |
| number of samples between successive frame |hop_length | 512 |
| length of window | win_length | None |
| Window function | window | 'hann' |
| Pad signal to center frame | True |
| Use padding mode at edges | pad_mode | 'constant' |
| Exponent for the magnitude melspectrogram. e.g., 1 for energy, 2 for power, etc. | power | 2.0 |

## Mel Filters

| Name | Parameter | Default Value | 
| --- | --- | --- | --- |
| number of Mel bands to generate | n_mels |  128 |
| lowest frequency (in Hz) | fmin |  0.0 |
| highest frequency (in Hz). If None, use fmax = sr / 2.0 | fmax |  None |
| se HTK formula instead of Slaney | htk |  False |
| Normalize function | norm |  'slaney' |
| Data of output basis, defaults to 32-bit (single-precision) | dtype |  <class 'numpy.float32'>
