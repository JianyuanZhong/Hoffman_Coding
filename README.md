# Hoffman_Coding

lossless data encoding algorithm to compress and decompress files.
Process behind is to count frequency of each character using Harsh Map. 
Build a Hoffman Tree according to their frequency, traverse the Huffman Tree and assign codes to characters and rewrite file with assigned codes. 
For decompression, rebuild Hoffman Tree with recorded frequency of each character, and "translate" the compressed file with tree. 
Project can be applied to both .txt and .jpg files. 
