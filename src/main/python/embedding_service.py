from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from sentence_transformers import SentenceTransformer
import numpy as np
from typing import List, Dict
import uvicorn

app = FastAPI()

# Load models
models = {
    "minilm": SentenceTransformer('sentence-transformers/all-MiniLM-L6-v2'),
    "multilingual": SentenceTransformer('paraphrase-multilingual-MiniLM-L12-v2'),
    "distiluse": SentenceTransformer('distiluse-base-multilingual-cased-v1')
}

class TextRequest(BaseModel):
    text: str

class SimilarityRequest(BaseModel):
    text1: str
    text2: str
    model: str = "minilm"

@app.post("/embed")
async def get_embedding(request: TextRequest, model: str = "minilm"):
    try:
        if model not in models:
            raise HTTPException(status_code=400, detail=f"Model {model} not supported")
        
        # Get embedding
        embedding = models[model].encode(request.text)
        return {"embedding": embedding.tolist()}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/similarity")
async def calculate_similarity(request: SimilarityRequest):
    try:
        if request.model not in models:
            raise HTTPException(status_code=400, detail=f"Model {request.model} not supported")
        
        # Get embeddings
        embedding1 = models[request.model].encode(request.text1)
        embedding2 = models[request.model].encode(request.text2)
        
        # Calculate cosine similarity
        similarity = np.dot(embedding1, embedding2) / (np.linalg.norm(embedding1) * np.linalg.norm(embedding2))
        
        return {
            "similarity": float(similarity),
            "model": request.model
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=5000) 