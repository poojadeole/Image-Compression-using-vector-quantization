# Image-Compression-using-vector-quantization
Built a tool to compress grayscale and RGB images using vector quantization which works on distributing probability density functions using pixel vectors and codebook using Java with accuracy of 96.4% in compressed image.

Here are the steps that you need to implement to compress an image.

1.Understanding your two pixel vector space to see what vectors your image contains
2.Initialization of codewords - select N initial codewords
3.Clustering vectors around each code word
4.Refine and Update your code words depending on outcome of 3. Repeat steps 3 and 4 until code words don’t change or the change is very minimal.
5.Quantize input vectors to produce output image











