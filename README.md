# Image-Compression-using-vector-quantization
Built a tool to compress grayscale and RGB images using vector quantization which works on distributing probability density functions using pixel vectors and codebook using Java with accuracy of 96.4% in compressed image.

Vector Quantization

Quantization or more formally scalar quantization, as you know, is a way to represent (or code) one sample of a continuous signal with a discrete value. Vector quantization on the contrary codes a group or block of samples (or a vectsor of samples) using a single discrete value or index. 
Why does this work, or why should this work? Most natural images are not a random collection of pixels but have very smooth varying areas – where pixels are not changing rapidly. Consequently, we could pre-decide a codebook of vectors, each vector represented by a block of two pixels (or four pixels etc) and then replace all similar looking blocks in the image with one of the code vectors. The number of vectors, or the length of the code book used, will depend on how much error you are willing to tolerate in your compression. More vectors will result in larger coding indexes (and hence less compression) but results are perceptually better and vice versa. Thus vector quantization may be described as a lossy compression technique where groups or blocks of samples are given one index that represents a code word. In general this can work in k dimensions, but we will limit your implementation to two dimensions and perform vector quantization on an image.
When forming vector quantization you need to create a code book, – the size or type of vector you will use and the number of vectors. Let’s assume that your vectors are two adjacent pixels side by side. For your assignment you will take as input a parameter N, which is the number of vectors in your codebook. You may assume this is a N is a power of 2 and thus after quantization each vector will need an index with logN bits. Your code will be called as follows and will result in a side by side display of the original image and your result after vector compression and decompression.
Here are the steps that you need to implement to compress an image.
1.	Understanding your two pixel vector space to see what vectors your image contains
2.	Initialization of codewords - select N initial codewords
3.	Clustering vectors around each code word 
4.	Refine and Update your code words depenut image. Be sure to show them side by side.

*We are given images in rgb format to work with.












