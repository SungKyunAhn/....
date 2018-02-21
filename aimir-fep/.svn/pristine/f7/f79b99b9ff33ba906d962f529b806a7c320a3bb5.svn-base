#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdarg.h>
#include <errno.h>
#include <signal.h>
#include <unistd.h>
#include <semaphore.h>
#include <fcntl.h>
#include <termios.h>
#include <sys/ioctl.h>
#include <sys/time.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/stat.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <sys/stat.h>
#include <dirent.h>
#include "zlib.h"

typedef struct	_tagIF4_HEADER
{
		unsigned char	soh;					// Satr Of Header 
		unsigned char	seq;					// Sequence Number
		unsigned char	attr;					// Communication Attribute
		unsigned int	len;					// Data Length
		unsigned char	svc;					// Service Type
}	__attribute__ ((packed)) IF4_HEADER, *PIF4_HEADER;

typedef struct	_tagIF4_TAIL
{
		unsigned short	crc;					// CRC 16
}	__attribute__ ((packed)) IF4_TAIL, *PIF4_TAIL;

typedef struct	_tagIF4_COMPRESS_HEADER
{
		unsigned char	compress;				// Compress type (0:None, 1:zlib, 2:gzip)
		unsigned int	source_size;			// Source size
}	__attribute__ ((packed)) IF4_COMPRESS_HEADER, *PIF4_COMPRESS_HEADER;

unsigned int GetFileSize(char *pszFileName)
{
	struct 	stat statbuf;
	int		fd;

	fd = open(pszFileName, O_RDONLY);
	if (fd <= 0)
		return 0;
	fstat(fd, &statbuf);
	close(fd);

	return (unsigned int)statbuf.st_size;
}

int main(int argc, char **argv)
{
	IF4_COMPRESS_HEADER	chdr;
	IF4_HEADER	*pHeader;
	char	*pszSource, *pszTarget;
	char	*pszBuffer, *pszDest;
	FILE	*fp;
	uLong	nDestLen;
	int		n, nFileSize, nSourceSize, nError;
//	int		nLength;

	// 파라메터의 개수 확인
	if (argc != 3)
	{
		printf("Usage: uncompress <Compress Filename> <Uncompress Filename>\r\n");
		return -1;
	}

	// 파일명칭을 받아온다.
	pszSource = argv[1];			// 압축된 파일명
	pszTarget = argv[2];			// 압축 해제할 파일명

	nFileSize = GetFileSize(pszSource);
	if ((nFileSize == 0) || (sizeof(IF4_COMPRESS_HEADER) > (unsigned int)nFileSize))
	{
		printf("uncompress: invalid source file size: %0d\r\n", nFileSize);
		return -2;
	}

	fp = fopen(pszSource, "rb");
	if (fp == NULL)
	{
		// 파일 개방 오류
		printf("uncompress: cannot open source file: %s\r\n", pszSource);
		return -3;
	}

	// 압축 해더를 읽는다.
	n = fread(&chdr, 1, sizeof(IF4_COMPRESS_HEADER), fp);
	if (n <= 0)
	{
		// 해더 읽기 실패
		printf("uncompress: invalid source file size: %s\r\n", pszSource);
		fclose(fp);
		return -4;
	}

	// 압축 유형이 zlib인지 확인한다.
	if (chdr.compress != 1)
	{
		printf("uncompress: invalid compress type: %0d\r\n", chdr.compress);
		fclose(fp);
		return -5;
	}

	// 길이가 잘못되었는지 확인한다.
	if (chdr.source_size == 0)
	{
		printf("uncompress: invalid target size: %0d\r\n", chdr.source_size);
		fclose(fp);
		return -6;
	}

	// 버퍼를 할당한다.
	nFileSize = nFileSize - sizeof(IF4_COMPRESS_HEADER);
	pszBuffer = (char *)malloc(nFileSize);
	if (pszBuffer == NULL)
	{
		printf("uncompress: memory allocation fail: %0d\r\n", nFileSize);
		fclose(fp);
		return -7;
	}	

	// 압축된 파일을 버퍼로 읽어 들인다.
	fread(pszBuffer, nFileSize, 1, fp);
	fclose(fp);

	// 압축 해제할 버퍼를 할당한다.
	nSourceSize = chdr.source_size;
	pszDest = (char *)malloc(nSourceSize);
	if (pszDest == NULL)
	{
		printf("uncompress: memory allocation fail: %0d\r\n", nSourceSize);
		free(pszBuffer);
		return -8;
	}	

	// 해더 부분을 복사한다.
	memcpy(pszDest, pszBuffer, sizeof(IF4_HEADER));

	// 압축을 해제한다.
	nDestLen = nSourceSize - (sizeof(IF4_HEADER) + sizeof(IF4_TAIL));
	nError = uncompress((Bytef*)&pszDest[sizeof(IF4_HEADER)], &nDestLen,
							(const Bytef*)&pszBuffer[sizeof(IF4_HEADER)],
							nFileSize-(sizeof(IF4_HEADER)+sizeof(IF4_TAIL)));

	// 목적 파일에 해제된 내용을 저장한다.
	if ((nDestLen > 0) && (nError == Z_OK))
	{
//		nLength = nDestLen + sizeof(IF4_HEADER) + sizeof(IF4_TAIL);
		pHeader = (IF4_HEADER *)pszDest;
		pHeader->len = nDestLen;

		fp = fopen(pszTarget, "wb");
		if (fp == NULL)
		{
			printf("uncompress: cannot open target file: %s\r\n", pszTarget);
			free(pszBuffer);
			free(pszDest);
			return -9;
		}

		fwrite(pszDest, nSourceSize, 1, fp);
		fclose(fp);
	}
	else
	{
		printf("uncompress: uncompress error: %d\r\n", nError);
		free(pszBuffer);
		free(pszDest);
		return -10;
	}

	// 할당된 메모리를 해제한다.
	free(pszBuffer);
	free(pszDest);
	return 0;
}

