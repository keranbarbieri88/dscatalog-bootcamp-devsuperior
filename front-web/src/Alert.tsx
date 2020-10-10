import React from 'react';


type Props = {
    text: string;
}

/* criação de componente dinâmico, toda vez que eu utilizar o
componente Alert sou obrigada a passar um texto lá no Alert da classe App
o text dentro do Alert é imutável  */
const Alert = ({ text } : Props) => (
    <div className="alert alert-primary">
        Hello {text}!
    </div>
);
export default Alert;
    