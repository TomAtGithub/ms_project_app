# TODO

- [x] create git repo
- [x] Record Audio
- [x] Save audio
- [x] Play Audio
- [x] Read file from external storage
- [x] Run openSMILE
- [ ] Create mfcc
- [ ] Try to make openSMILE mfcc like librosa mfcc
- [ ] Load model
- [ ] Classify mfcc
- [ ] Show label in GUI
- [ ] Load, classify and save results.csv of example audio on start option
- [ ] Evaluate dialog
- [ ] share evaluation / send via mail

# Generate openSMILE config

openSMILE configs can be generated via SMILExtract.
See here for available options and pre configurations:
- https://audeering.github.io/opensmile/reference.html#components
- https://audeering.github.io/opensmile/get-started.html?highlight=android#extracting-features-for-emotion-recognition

    #Example to generate config for mfcc
    SMILExtract -cfgFileTemplate -configDflt cWaveSource,cFramer,cWindower,cTransformFFT,cFFTmagphase,cMelspec,cMfcc,cDeltaRegression,cDeltaRegression,cCsvSink -l 1 2> mfcc.config